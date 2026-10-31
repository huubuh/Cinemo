package com.moviebooking.backend.dto.request;

import com.moviebooking.backend.util.EmailUtils;
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
