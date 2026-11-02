package com.moviebooking.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoogleLoginRequest(
        @NotBlank(message = "Đăng nhập Google thất bại, vui lòng thử lại")
        @Size(max = 16384, message = "Đăng nhập Google thất bại, vui lòng thử lại") String idToken
) {
    @Override
    public String toString() {
        return "GoogleLoginRequest[redacted]";
    }
}
