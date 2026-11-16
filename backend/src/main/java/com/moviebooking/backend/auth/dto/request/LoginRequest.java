package com.moviebooking.backend.auth.dto.request;

import com.moviebooking.backend.auth.util.EmailUtils;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Email không được để trống")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        String password

) {
    public LoginRequest {
        email = EmailUtils.normalize(email);
    }
}
