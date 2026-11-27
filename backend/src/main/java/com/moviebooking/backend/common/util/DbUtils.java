package com.moviebooking.backend.common.util;

import java.sql.SQLException;

public final class DbUtils {

    private static final String UNIQUE_VIOLATION = "23505";

    private DbUtils() {}

    public static boolean isUniqueViolation(Throwable error) {
        for (Throwable t = error; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && UNIQUE_VIOLATION.equals(sql.getSQLState())) {
                return true;
            }
        }
        return false;
    }
}
