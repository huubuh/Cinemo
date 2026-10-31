package com.moviebooking.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "password_reset_otps")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetOtp extends BaseEntity {
    public static final Duration TTL = Duration.ofMinutes(5);

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(unique = true)
    private User user;
    @Column(length = 64)
    private String otpHash;
    private Instant expiresAt;
    private int failedAttempts;
    private Instant lastSentAt;
}
