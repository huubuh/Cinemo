package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.dto.request.ChangePasswordRequest;
import com.moviebooking.backend.dto.request.ForgotPasswordRequest;
import com.moviebooking.backend.dto.request.ResetPasswordRequest;
import com.moviebooking.backend.exception.AppException;
import com.moviebooking.backend.exception.ErrorCode;
import com.moviebooking.backend.model.PasswordResetOtp;
import com.moviebooking.backend.model.User;
import com.moviebooking.backend.model.enums.AccountStatus;
import com.moviebooking.backend.repository.PasswordResetOtpRepository;
import com.moviebooking.backend.repository.RefreshTokenRepository;
import com.moviebooking.backend.repository.UserRepository;
import com.moviebooking.backend.security.JwtTokenProvider;
import com.moviebooking.backend.service.EmailService;
import com.moviebooking.backend.service.PasswordResetOtpService;
import com.moviebooking.backend.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetOtpService otpService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // Không dùng transaction: gửi mail chậm không được giữ kết nối DB.
    // Mọi trường hợp đều trả cùng một phản hồi để không lộ email nào đã đăng ký.
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);
        if (user == null || user.getStatus() != AccountStatus.ACTIVE) {
            log.info("Bỏ qua gửi OTP: tài khoản không tồn tại hoặc bị khóa");
            return;
        }
        PasswordResetOtp previous = otpRepository.findByUserId(user.getId()).orElse(null);
        if (previous != null && previous.getLastSentAt().plusSeconds(60).isAfter(Instant.now())) {
            log.info("Bỏ qua gửi OTP: chưa hết 60 giây chờ gửi lại");
            return;
        }
        String otp = String.format(Locale.ROOT, "%06d", SECURE_RANDOM.nextInt(1_000_000));
        try {
            emailService.sendPasswordResetOtp(user.getEmail(), otp);
            otpService.storeSentOtp(user.getId(), jwtTokenProvider.hash(otp),
                    previous == null ? null : previous.getVersion());
        } catch (MailException | OptimisticLockingFailureException | DataIntegrityViolationException e) {
            // Lỗi mail có thể chứa nội dung thư hoặc thông tin đăng nhập: chỉ log tên lỗi
            log.warn("Gửi OTP thất bại ({})", e.getClass().getSimpleName());
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Kiểm tra định dạng trước khi tìm user và trừ lượt: không lộ email, không tốn lượt thử
        ensureValidFormat(request.newPassword());
        User user = userRepository.findByEmail(request.email())
                .filter(u -> u.getStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));
        // Giữ chỗ 1 lượt thử trước khi so sánh; hết lượt, hết hạn hoặc đã dùng thì từ chối luôn
        if (!otpService.reserveAttempt(user.getId())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        // Đọc OTP sau khi giữ chỗ để có version mới nhất
        PasswordResetOtp otp = otpRepository.findByUserId(user.getId())
                .filter(o -> o.getOtpHash() != null)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));
        if (!MessageDigest.isEqual(otp.getOtpHash().getBytes(StandardCharsets.US_ASCII),
                jwtTokenProvider.hash(request.otp()).getBytes(StandardCharsets.US_ASCII))) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        updatePassword(user, request.newPassword());
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }
        ensureValidFormat(request.newPassword());
        updatePassword(user, request.newPassword());
    }

    private void ensureValidFormat(String newPassword) {
        // @Size đếm ký tự; BCrypt giới hạn 72 byte (chữ có dấu chiếm 2-3 byte)
        if (newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new AppException(ErrorCode.INVALID_NEW_PASSWORD);
        }
    }

    private void updatePassword(User user, String newPassword) {
        // Chỉ kiểm tra trùng mật khẩu cũ sau khi đã xác thực, tránh bị dùng để dò mật khẩu
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_NEW_PASSWORD);
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        otpRepository.findByUserId(user.getId()).ifPresent(otp -> otp.setOtpHash(null));
        userRepository.saveAndFlush(user);
        refreshTokenRepository.revokeAllByUserId(user.getId(), Instant.now());
    }
}
