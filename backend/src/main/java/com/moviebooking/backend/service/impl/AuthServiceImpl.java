package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.dto.request.ChangePasswordRequest;
import com.moviebooking.backend.dto.request.ForgotPasswordRequest;
import com.moviebooking.backend.dto.request.LoginRequest;
import com.moviebooking.backend.dto.request.RefreshTokenRequest;
import com.moviebooking.backend.dto.request.RegisterRequest;
import com.moviebooking.backend.dto.request.ResetPasswordRequest;
import com.moviebooking.backend.dto.response.AuthResponse;
import com.moviebooking.backend.dto.response.UserResponse;
import com.moviebooking.backend.exception.AppException;
import com.moviebooking.backend.exception.ErrorCode;
import com.moviebooking.backend.mapper.UserMapper;
import com.moviebooking.backend.model.RefreshToken;
import com.moviebooking.backend.model.User;
import com.moviebooking.backend.model.enums.AccountStatus;
import com.moviebooking.backend.repository.RefreshTokenRepository;
import com.moviebooking.backend.repository.UserRepository;
import com.moviebooking.backend.security.JwtTokenProvider;
import com.moviebooking.backend.service.AuthService;
import com.moviebooking.backend.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordService passwordService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));
        ensureActive(user);
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(jwtTokenProvider.hash(request.refreshToken()))
                .filter(t -> t.getRevokedAt() == null && t.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));
        User user = refreshToken.getUser();
        ensureActive(user);
        // Xoay vòng: token cũ bị thu hồi, cấp cặp token mới
        refreshToken.setRevokedAt(Instant.now());
        return issueTokens(user);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByTokenHash(jwtTokenProvider.hash(request.refreshToken()))
                .filter(t -> t.getRevokedAt() == null)
                .ifPresent(t -> t.setRevokedAt(Instant.now()));
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        passwordService.forgotPassword(request);
    }

    // Bắt xung đột version ngoài transaction: hai request dùng cùng một mã thì request sau báo OTP không hợp lệ
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        try {
            passwordService.resetPassword(request);
        } catch (OptimisticLockingFailureException e) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
    }

    @Override
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        passwordService.changePassword(userId, request);
    }

    private AuthResponse issueTokens(User user) {
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(jwtTokenProvider.hash(rawRefreshToken));
        refreshToken.setExpiresAt(Instant.now().plus(JwtTokenProvider.REFRESH_TOKEN_TTL));
        refreshTokenRepository.save(refreshToken);
        return new AuthResponse(jwtTokenProvider.generateAccessToken(user), rawRefreshToken);
    }

    private void ensureActive(User user) {
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
    }
}
