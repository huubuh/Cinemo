package com.moviebooking.backend.auth.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
