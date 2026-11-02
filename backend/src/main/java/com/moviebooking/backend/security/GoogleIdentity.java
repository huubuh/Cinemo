package com.moviebooking.backend.security;

// Chỉ tạo sau khi đã xác minh chữ ký và các claim của Google ID token.
public record GoogleIdentity(String subject, String email, String name) {}
