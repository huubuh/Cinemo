package com.moviebooking.backend.dto.request;

import jakarta.validation.constraints.*;

public record ChangePasswordRequest(
        @NotBlank(message = "Mật khẩu hiện tại không được để trống")
        String currentPassword,
        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 8, max = 72, message = "Mật khẩu mới phải từ 8 đến 72 ký tự")
        String newPassword
) {
    @Override
    public String toString() {
        return "ChangePasswordRequest[redacted]";
    }
}
