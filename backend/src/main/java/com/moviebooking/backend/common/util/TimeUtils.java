package com.moviebooking.backend.common.util;

import java.time.LocalDate;
import java.time.ZoneId;

public final class TimeUtils {

    public static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private TimeUtils() {}

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }
}
