package com.moviebooking.backend.service;

import java.util.UUID;

public interface PasswordResetOtpService {
    void storeSentOtp(UUID userId, String hash, Long expectedVersion);

    boolean reserveAttempt(UUID userId);
}
