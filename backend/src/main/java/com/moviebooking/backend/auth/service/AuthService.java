package com.moviebooking.backend.auth.service;

import com.moviebooking.backend.auth.dto.request.EmailRequest;
import com.moviebooking.backend.auth.dto.request.LoginRequest;
import com.moviebooking.backend.auth.dto.request.RefreshTokenRequest;
import com.moviebooking.backend.auth.dto.request.RegisterRequest;
import com.moviebooking.backend.auth.dto.request.VerifyEmailRequest;
import com.moviebooking.backend.auth.dto.response.AuthResponse;
import com.moviebooking.backend.auth.dto.response.UserResponse;
import com.moviebooking.backend.auth.security.GoogleIdentity;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse verifyEmail(VerifyEmailRequest request);

    void resendVerification(EmailRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse loginGoogle(GoogleIdentity identity);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}
