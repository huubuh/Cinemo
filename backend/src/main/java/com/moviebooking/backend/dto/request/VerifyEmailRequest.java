package com.moviebooking.backend.dto.request;

import com.moviebooking.backend.util.EmailUtils;
import jakarta.validation.constraints.*;

public record VerifyEmailRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 254, message = "Email tối đa 254 ký tự")
        String email,
        @NotBlank(message = "OTP không hợp lệ hoặc đã hết hạn")
        @Pattern(regexp = "[0-9]{6}", message = "OTP không hợp lệ hoặc đã hết hạn") String otp
) {
    public VerifyEmailRequest {
        email = EmailUtils.normalize(email);
    }

    @Override
    public String toString() {
        return "VerifyEmailRequest[redacted]";
    }
}
