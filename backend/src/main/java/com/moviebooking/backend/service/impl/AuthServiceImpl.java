package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.dto.request.EmailRequest;
import com.moviebooking.backend.dto.request.LoginRequest;
import com.moviebooking.backend.dto.request.RefreshTokenRequest;
import com.moviebooking.backend.dto.request.RegisterRequest;
import com.moviebooking.backend.dto.request.VerifyEmailRequest;
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
import com.moviebooking.backend.security.GoogleIdentity;
import com.moviebooking.backend.security.JwtTokenProvider;
import com.moviebooking.backend.service.AuthService;
import com.moviebooking.backend.service.EmailOtpService;
import com.moviebooking.backend.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String VERIFY_PURPOSE = "xác minh email";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordService passwordService;
    private final EmailOtpService emailOtpService;

    @Override
    public UserResponse register(RegisterRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);
        if (user == null) {
            user = userMapper.toEntity(request);
        } else if (user.isEmailVerified()) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        } else {
            // Chưa xác minh thì cho đăng ký lại: chống người khác đăng ký trước bằng email không phải của họ
            emailOtpService.ensureCanSend(user.getId());
            user.setFullName(request.fullName());
        }
        user.setPasswordHash(passwordService.encode(request.password()));
        user = userRepository.save(user);
        emailOtpService.send(user, VERIFY_PURPOSE);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(u -> !u.isEmailVerified())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));
        user.ensureActive();
        emailOtpService.verify(user.getId(), request.otp());
        user.setEmailVerified(true);
        return issueTokens(user);
    }

    @Override
    public void resendVerification(EmailRequest request) {
        userRepository.findByEmail(request.email())
                .filter(u -> !u.isEmailVerified() && u.getStatus() == AccountStatus.ACTIVE)
                .ifPresent(u -> emailOtpService.send(u, VERIFY_PURPOSE));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(u -> u.getPasswordHash() != null)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));
        user.ensureActive();
        if (!user.isEmailVerified()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse loginGoogle(GoogleIdentity identity) {
        User user = userRepository.findByGoogleSubject(identity.subject())
                .orElseGet(() -> linkOrCreate(identity));
        user.ensureActive();
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(jwtTokenProvider.hash(request.refreshToken()))
                .filter(t -> t.getRevokedAt() == null && t.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));
        User user = refreshToken.getUser();
        user.ensureActive();
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

    // Google đã xác minh email nên gộp vào tài khoản trùng email, hoặc tạo mới
    private User linkOrCreate(GoogleIdentity identity) {
        User user = userRepository.findByEmail(identity.email()).orElseGet(() -> userMapper.toEntity(identity));
        if (user.getGoogleSubject() != null) {
            throw new AppException(ErrorCode.GOOGLE_LINK_CONFLICT);
        }
        if (!user.isEmailVerified()) {
            // Mật khẩu do người chưa chứng minh sở hữu email đặt, không tin được
            user.setPasswordHash(null);
            user.setEmailVerified(true);
        }
        user.setGoogleSubject(identity.subject());
        return userRepository.save(user);
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
}
