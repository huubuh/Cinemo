package com.moviebooking.backend.service;

public interface EmailService {
    void sendPasswordResetOtp(String email, String otp);
}
