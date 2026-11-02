package com.moviebooking.backend.service.impl;

import com.moviebooking.backend.model.EmailOtp;
import com.moviebooking.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendOtp(String email, String otp, String purpose) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(email);
        message.setSubject("Cinemo - Mã OTP " + purpose);
        message.setText("Mã OTP " + purpose + " của bạn là: " + otp
                + "\nMã có hiệu lực trong " + EmailOtp.TTL.toMinutes() + " phút và chỉ sử dụng được một lần."
                + "\nNếu bạn không yêu cầu " + purpose + ", hãy bỏ qua email này.");
        mailSender.send(message);
    }
}
