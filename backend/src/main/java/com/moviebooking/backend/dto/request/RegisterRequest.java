package com.moviebooking.backend.dto.request;

import com.moviebooking.backend.util.EmailUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 254, message = "Email tối đa 254 ký tự")
        String email,

        // BCrypt chỉ dùng 72 byte đầu của mật khẩu
        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 8, max = 72, message = "Mật khẩu phải từ 8 đến 72 ký tự")
        String password,

        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
        String fullName

) {
    public RegisterRequest {
        email = EmailUtils.normalize(email);
    }
}
