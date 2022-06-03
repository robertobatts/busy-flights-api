package com.travix.medusa.busyflights.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private DateUtils() {
    }

    public static String reformatStringDate(String date, DateTimeFormatter in, DateTimeFormatter out) {
        return LocalDateTime.parse(date, in).format(out);
    }
}
