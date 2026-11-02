package com.moviebooking.backend.service;

import com.moviebooking.backend.model.User;
import java.util.UUID;

// Một mã OTP qua email cho mỗi user, dùng cho xác minh email và đặt lại mật khẩu
public interface EmailOtpService {
    void ensureCanSend(UUID userId);

    void send(User user, String purpose);

    // Cần chạy trong transaction của caller
    void verify(UUID userId, String otp);
}
