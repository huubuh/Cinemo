package com.moviebooking.backend.util;

import java.util.Locale;

public final class EmailUtils {

    private EmailUtils() {
    }

    // Quy tắc chuẩn hóa email duy nhất của hệ thống: bỏ khoảng trắng, chuyển chữ thường
    public static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
