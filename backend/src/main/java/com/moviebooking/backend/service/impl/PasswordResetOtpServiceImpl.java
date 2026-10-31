package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.model.PasswordResetOtp;
import com.moviebooking.backend.repository.PasswordResetOtpRepository;
import com.moviebooking.backend.repository.UserRepository;
import com.moviebooking.backend.service.PasswordResetOtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetOtpServiceImpl implements PasswordResetOtpService {

    private final PasswordResetOtpRepository otpRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void storeSentOtp(UUID userId, String hash, Long expectedVersion) {
        PasswordResetOtp otp = otpRepository.findByUserId(userId).orElseGet(() -> {
            PasswordResetOtp created = new PasswordResetOtp();
            created.setUser(userRepository.getReferenceById(userId));
            return created;
        });
        // Request gửi mail chậm không được ghi đè mã mới hơn
        if (!Objects.equals(otp.getVersion(), expectedVersion)) {
            log.info("Bỏ qua lưu OTP: đã có mã mới hơn");
            return;
        }
        Instant now = Instant.now();
        otp.setOtpHash(hash);
        otp.setExpiresAt(now.plus(PasswordResetOtp.TTL));
        otp.setLastSentAt(now);
        otp.setFailedAttempts(0);
        otpRepository.save(otp);
    }

    // Transaction riêng: lượt thử được lưu kể cả khi request đặt lại mật khẩu bị rollback
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean reserveAttempt(UUID userId) {
        return otpRepository.reserveAttempt(userId, Instant.now()) == 1;
    }
}
