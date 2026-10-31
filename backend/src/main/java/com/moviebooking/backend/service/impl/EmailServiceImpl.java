package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.model.PasswordResetOtp;
import com.moviebooking.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendPasswordResetOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Cinemo - Mã OTP đặt lại mật khẩu");
        message.setText("Mã OTP đặt lại mật khẩu của bạn là: " + otp
                + "\nMã có hiệu lực trong " + PasswordResetOtp.TTL.toMinutes() + " phút và chỉ sử dụng được một lần."
                + "\nNếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.");
        mailSender.send(message);
    }
}
