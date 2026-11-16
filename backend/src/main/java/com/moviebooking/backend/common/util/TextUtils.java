package com.moviebooking.backend.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TextUtils {
    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");

    private TextUtils() {}

    public static String stripToNull(String value) {
        if (value == null) {
            return null;
        }
        String stripped = value.strip();
        return stripped.isEmpty() ? null : Normalizer.normalize(stripped, Normalizer.Form.NFC);
    }

    public static String removeVietnameseAccents(String value) {
        String normalized = Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return COMBINING_MARKS.matcher(normalized).replaceAll("").replace('đ', 'd');
    }
}
