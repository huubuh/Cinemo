package com.moviebooking.backend.auth.service;

import com.moviebooking.backend.auth.model.User;
import java.util.UUID;

// Một mã OTP qua email cho mỗi user, dùng cho xác minh email và đặt lại mật khẩu
public interface EmailOtpService {
    void ensureCanSend(UUID userId);

    void send(User user, String purpose);

    // Cần chạy trong transaction của caller
    void verify(UUID userId, String otp);
}
