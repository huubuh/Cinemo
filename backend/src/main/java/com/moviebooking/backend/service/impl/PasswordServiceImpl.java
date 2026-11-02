package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.dto.request.ChangePasswordRequest;
import com.moviebooking.backend.dto.request.EmailRequest;
import com.moviebooking.backend.dto.request.ResetPasswordRequest;
import com.moviebooking.backend.exception.AppException;
import com.moviebooking.backend.exception.ErrorCode;
import com.moviebooking.backend.model.User;
import com.moviebooking.backend.model.enums.AccountStatus;
import com.moviebooking.backend.repository.RefreshTokenRepository;
import com.moviebooking.backend.repository.UserRepository;
import com.moviebooking.backend.service.EmailOtpService;
import com.moviebooking.backend.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailOtpService emailOtpService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String rawPassword) {
        ensureWithinLimit(rawPassword);
        return passwordEncoder.encode(rawPassword);
    }

    // Mọi trường hợp đều trả cùng một phản hồi để không lộ email nào đã đăng ký.
    @Override
    public void forgotPassword(EmailRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);
        if (user == null || user.getStatus() != AccountStatus.ACTIVE) {
            log.info("Bỏ qua gửi OTP: tài khoản không tồn tại hoặc bị khóa");
            return;
        }
        try {
            emailOtpService.send(user, "đặt lại mật khẩu");
        } catch (AppException | MailException e) {
            // Chưa hết thời gian chờ hoặc lỗi mail; lỗi mail có thể chứa thông tin đăng nhập nên chỉ log tên lỗi
            log.warn("Không gửi OTP ({})", e.getClass().getSimpleName());
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Kiểm tra độ dài trước khi tìm user và trừ lượt: không lộ email, không tốn lượt thử
        ensureWithinLimit(request.newPassword());
        User user = userRepository.findByEmail(request.email())
                .filter(u -> u.getStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));
        emailOtpService.verify(user.getId(), request.otp());
        // Nhận được OTP qua email là đã chứng minh sở hữu email
        user.setEmailVerified(true);
        updatePassword(user, request.newPassword());
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.ensureActive();
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }
        updatePassword(user, request.newPassword());
    }

    private void ensureWithinLimit(String rawPassword) {
        // @Size đếm ký tự; BCrypt giới hạn 72 byte (chữ có dấu chiếm 2-3 byte)
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new AppException(ErrorCode.PASSWORD_TOO_LONG);
        }
    }

    private void updatePassword(User user, String newPassword) {
        // Chỉ kiểm tra trùng mật khẩu cũ sau khi đã xác thực, tránh bị dùng để dò mật khẩu
        if (user.getPasswordHash() != null && passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_NEW_PASSWORD);
        }
        user.setPasswordHash(encode(newPassword));
        refreshTokenRepository.revokeAllByUserId(user.getId(), Instant.now());
    }
}
