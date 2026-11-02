package com.moviebooking.backend.service;

import com.moviebooking.backend.dto.request.EmailRequest;
import com.moviebooking.backend.dto.request.LoginRequest;
import com.moviebooking.backend.dto.request.RefreshTokenRequest;
import com.moviebooking.backend.dto.request.RegisterRequest;
import com.moviebooking.backend.dto.request.VerifyEmailRequest;
import com.moviebooking.backend.dto.response.AuthResponse;
import com.moviebooking.backend.dto.response.UserResponse;
import com.moviebooking.backend.security.GoogleIdentity;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse verifyEmail(VerifyEmailRequest request);

    void resendVerification(EmailRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse loginGoogle(GoogleIdentity identity);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}
