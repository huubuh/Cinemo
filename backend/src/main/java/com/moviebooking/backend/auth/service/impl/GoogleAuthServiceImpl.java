package com.moviebooking.backend.auth.service.impl;

import com.moviebooking.backend.auth.security.GoogleIdentity;
import com.moviebooking.backend.auth.dto.request.GoogleLoginRequest;
import com.moviebooking.backend.auth.dto.response.AuthResponse;
import com.moviebooking.backend.common.exception.AppException;
import com.moviebooking.backend.common.exception.ErrorCode;
import com.moviebooking.backend.auth.security.GoogleTokenVerifier;
import com.moviebooking.backend.auth.service.AuthService;
import com.moviebooking.backend.auth.service.GoogleAuthService;
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
        // Xác minh với Google trước, để không giữ kết nối DB trong lúc chờ Google
        GoogleIdentity identity = verifier.verify(request.idToken());
        try {
            return authService.loginGoogle(identity);
        } catch (DataIntegrityViolationException | OptimisticLockingFailureException exception) {
            // Request song song vừa tạo hoặc gộp cùng tài khoản
            throw new AppException(ErrorCode.GOOGLE_LINK_CONFLICT);
        }
    }
}
