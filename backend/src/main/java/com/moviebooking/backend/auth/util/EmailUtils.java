package com.moviebooking.backend.auth.util;

import java.util.Locale;

public final class EmailUtils {

    private EmailUtils() {
    }

    // Chuẩn hóa email duy nhất của hệ thống: trim + chữ thường
    public static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
