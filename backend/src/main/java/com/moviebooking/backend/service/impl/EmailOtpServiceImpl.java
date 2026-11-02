package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.exception.AppException;
import com.moviebooking.backend.exception.ErrorCode;
import com.moviebooking.backend.model.EmailOtp;
import com.moviebooking.backend.model.User;
import com.moviebooking.backend.repository.EmailOtpRepository;
import com.moviebooking.backend.repository.UserRepository;
import com.moviebooking.backend.security.JwtTokenProvider;
import com.moviebooking.backend.service.EmailOtpService;
import com.moviebooking.backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class EmailOtpServiceImpl implements EmailOtpService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailOtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TransactionTemplate newTransaction;

    public EmailOtpServiceImpl(EmailOtpRepository otpRepository, UserRepository userRepository,
                               EmailService emailService, JwtTokenProvider jwtTokenProvider,
                               PlatformTransactionManager transactionManager) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.newTransaction = new TransactionTemplate(transactionManager);
        this.newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public void ensureCanSend(UUID userId) {
        otpRepository.findByUserId(userId).ifPresent(this::ensureCooledDown);
    }

    // Gọi ngoài transaction: gửi mail chậm không được giữ kết nối DB
    @Override
    public void send(User user, String purpose) {
        EmailOtp previous = otpRepository.findByUserId(user.getId()).orElse(null);
        if (previous != null) {
            ensureCooledDown(previous);
        }
        String otp = String.format(Locale.ROOT, "%06d", SECURE_RANDOM.nextInt(1_000_000));
        emailService.sendOtp(user.getEmail(), otp, purpose);
        Long expectedVersion = previous == null ? null : previous.getVersion();
        try {
            newTransaction.executeWithoutResult(status ->
                    store(user.getId(), jwtTokenProvider.hash(otp), expectedVersion));
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException e) {
            log.info("Bỏ qua lưu OTP: request song song đã lưu mã khác");
        }
    }

    @Override
    public void verify(UUID userId, String otp) {
        // Transaction riêng: lượt thử được lưu kể cả khi request bị rollback
        Boolean reserved = newTransaction.execute(status -> otpRepository.reserveAttempt(userId, Instant.now()) == 1);
        // UPDATE có điều kiện: request song song dùng cùng mã sẽ chờ khóa dòng rồi nhận 0 dòng
        if (!Boolean.TRUE.equals(reserved)
                || otpRepository.consume(userId, jwtTokenProvider.hash(otp), Instant.now()) != 1) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
    }

    private void ensureCooledDown(EmailOtp otp) {
        if (otp.getLastSentAt().plusSeconds(60).isAfter(Instant.now())) {
            throw new AppException(ErrorCode.OTP_SEND_TOO_SOON);
        }
    }

    private void store(UUID userId, String hash, Long expectedVersion) {
        EmailOtp otp = otpRepository.findByUserId(userId).orElseGet(() -> {
            EmailOtp created = new EmailOtp();
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
        otp.setExpiresAt(now.plus(EmailOtp.TTL));
        otp.setLastSentAt(now);
        otp.setFailedAttempts(0);
        otpRepository.save(otp);
    }
}
