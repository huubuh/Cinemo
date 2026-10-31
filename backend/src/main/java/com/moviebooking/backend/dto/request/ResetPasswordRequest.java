package com.moviebooking.backend.dto.request;

import com.moviebooking.backend.util.EmailUtils;
import jakarta.validation.constraints.*;

public record ResetPasswordRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 254, message = "Email tối đa 254 ký tự")
        String email,
        @NotBlank(message = "OTP không hợp lệ hoặc đã hết hạn")
        @Pattern(regexp = "[0-9]{6}", message = "OTP không hợp lệ hoặc đã hết hạn") String otp,
        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 8, max = 72, message = "Mật khẩu mới phải từ 8 đến 72 ký tự")
        String newPassword
) {
    public ResetPasswordRequest {
        email = EmailUtils.normalize(email);
    }

    @Override
    public String toString() {
        return "ResetPasswordRequest[redacted]";
    }
}
