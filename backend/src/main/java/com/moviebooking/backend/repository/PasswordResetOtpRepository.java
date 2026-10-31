package com.moviebooking.backend.repository;

import com.moviebooking.backend.model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {
    Optional<PasswordResetOtp> findByUserId(UUID userId);

    // Tăng số lần thử bằng một câu UPDATE: DB xếp hàng các request song song, tối đa 5 lần/mã
    @Modifying
    @Query("update PasswordResetOtp o set o.failedAttempts = o.failedAttempts + 1, o.version = o.version + 1"
            + " where o.user.id = :userId and o.otpHash is not null"
            + " and o.failedAttempts < 5 and o.expiresAt > :now")
    int reserveAttempt(@Param("userId") UUID userId, @Param("now") Instant now);
}
