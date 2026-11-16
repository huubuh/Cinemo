package com.moviebooking.backend.auth.service;

public interface EmailService {
    void sendOtp(String email, String otp, String purpose);
}
