package com.moviebooking.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(

        @NotBlank(message = "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại")
        String refreshToken

) {}
