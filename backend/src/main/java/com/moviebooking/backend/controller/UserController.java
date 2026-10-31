package com.moviebooking.backend.controller;

import com.moviebooking.backend.dto.response.ApiResponse;
import com.moviebooking.backend.dto.response.UserResponse;
import com.moviebooking.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getById(UUID.fromString(jwt.getSubject())))
                .build();
    }
}
