package com.moviebooking.backend.service;

import com.moviebooking.backend.dto.request.ChangePasswordRequest;
import com.moviebooking.backend.dto.request.ForgotPasswordRequest;
import com.moviebooking.backend.dto.request.ResetPasswordRequest;
import java.util.UUID;

public interface PasswordService {
    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(UUID userId, ChangePasswordRequest request);
}
