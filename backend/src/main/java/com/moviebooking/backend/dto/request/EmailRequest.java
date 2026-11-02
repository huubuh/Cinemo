package com.moviebooking.backend.dto.request;

import com.moviebooking.backend.util.EmailUtils;
import jakarta.validation.constraints.*;

public record EmailRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 254, message = "Email tối đa 254 ký tự")
        String email
) {
    public EmailRequest {
        email = EmailUtils.normalize(email);
    }
}
