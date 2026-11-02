package com.moviebooking.backend.service;

import com.moviebooking.backend.dto.request.GoogleLoginRequest;
import com.moviebooking.backend.dto.response.AuthResponse;

public interface GoogleAuthService {
    AuthResponse login(GoogleLoginRequest request);
}
