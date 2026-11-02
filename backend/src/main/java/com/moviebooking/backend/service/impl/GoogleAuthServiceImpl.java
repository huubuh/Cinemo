package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.security.GoogleIdentity;
import com.moviebooking.backend.dto.request.GoogleLoginRequest;
import com.moviebooking.backend.dto.response.AuthResponse;
import com.moviebooking.backend.exception.AppException;
import com.moviebooking.backend.exception.ErrorCode;
import com.moviebooking.backend.security.GoogleTokenVerifier;
import com.moviebooking.backend.service.AuthService;
import com.moviebooking.backend.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private final GoogleTokenVerifier verifier;
    private final AuthService authService;

    @Override
    public AuthResponse login(GoogleLoginRequest request) {
        // Xác minh Google ngoài transaction; gọi qua proxy để lỗi commit được bắt ở đây.
        GoogleIdentity identity = verifier.verify(request.idToken());
        try {
            return authService.loginGoogle(identity);
        } catch (DataIntegrityViolationException | OptimisticLockingFailureException exception) {
            // Request song song vừa tạo hoặc gộp cùng tài khoản
            throw new AppException(ErrorCode.GOOGLE_LINK_CONFLICT);
        }
    }
}
