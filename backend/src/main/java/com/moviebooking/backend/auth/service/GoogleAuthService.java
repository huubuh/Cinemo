package com.moviebooking.backend.auth.service;

import com.moviebooking.backend.auth.dto.request.GoogleLoginRequest;
import com.moviebooking.backend.auth.dto.response.AuthResponse;

public interface GoogleAuthService {
    AuthResponse login(GoogleLoginRequest request);
}
