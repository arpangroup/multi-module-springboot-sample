package com.trustai.common.utils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateUtils {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM E, yyyy h:mm a", Locale.ENGLISH);

    private DateUtils() {
        // Utility class: prevent instantiation
    }

    /**
     * Formats a LocalDateTime to a human-readable string like:
     * "27 May Tue, 2025 1:28 PM"
     *
     * @param dateTime the LocalDateTime to format
     * @return formatted date string
     */
    public static String formatDisplayDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DISPLAY_FORMATTER);
    }

}