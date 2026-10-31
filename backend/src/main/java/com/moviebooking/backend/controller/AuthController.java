package com.moviebooking.backend.controller;

import com.moviebooking.backend.dto.request.ChangePasswordRequest;
import com.moviebooking.backend.dto.request.ForgotPasswordRequest;
import com.moviebooking.backend.dto.request.LoginRequest;
import com.moviebooking.backend.dto.request.RefreshTokenRequest;
import com.moviebooking.backend.dto.request.RegisterRequest;
import com.moviebooking.backend.dto.request.ResetPasswordRequest;
import com.moviebooking.backend.dto.response.ApiResponse;
import com.moviebooking.backend.dto.response.AuthResponse;
import com.moviebooking.backend.dto.response.UserResponse;
import com.moviebooking.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.login(request))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.refresh(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .message("Nếu tài khoản đủ điều kiện, mã OTP sẽ được gửi đến email của bạn.")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .message("Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.")
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal Jwt jwt,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(UUID.fromString(jwt.getSubject()), request);
        return ApiResponse.<Void>builder()
                .message("Đổi mật khẩu thành công. Vui lòng đăng nhập lại.")
                .build();
    }
}
