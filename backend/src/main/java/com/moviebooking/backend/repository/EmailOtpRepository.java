package com.moviebooking.backend.repository;

import com.moviebooking.backend.model.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, UUID> {
    Optional<EmailOtp> findByUserId(UUID userId);

    // Tăng số lần thử bằng một câu UPDATE: DB xếp hàng các request song song, tối đa 5 lần/mã
    @Modifying
    @Query("update EmailOtp o set o.failedAttempts = o.failedAttempts + 1, o.version = o.version + 1"
            + " where o.user.id = :userId and o.otpHash is not null"
            + " and o.failedAttempts < 5 and o.expiresAt > :now")
    int reserveAttempt(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("update EmailOtp o set o.otpHash = null"
            + " where o.user.id = :userId and o.otpHash = :hash and o.expiresAt > :now")
    int consume(@Param("userId") UUID userId, @Param("hash") String hash, @Param("now") Instant now);
}
