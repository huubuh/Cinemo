package com.moviebooking.backend.service;

import com.moviebooking.backend.dto.request.ChangePasswordRequest;
import com.moviebooking.backend.dto.request.EmailRequest;
import com.moviebooking.backend.dto.request.ResetPasswordRequest;
import java.util.UUID;

public interface PasswordService {
    String encode(String rawPassword);

    void forgotPassword(EmailRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(UUID userId, ChangePasswordRequest request);
}
