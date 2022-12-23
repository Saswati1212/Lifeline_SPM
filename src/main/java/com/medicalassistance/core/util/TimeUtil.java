package com.medicalassistance.core.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TimeUtil {
    /**
     * Gets the current time and converts into UTC time without changing the date and time.
     */
    public static ZonedDateTime nowUTC() {
        return toUTC(ZonedDateTime.now());
    }

    /**
     * Converts the date and time into UTC time without changing the date and time.
     */
    public static ZonedDateTime toUTC(ZonedDateTime zonedDateTime) {
        long offset = zonedDateTime.getOffset().getTotalSeconds();
        Instant nowInstant = Instant.ofEpochMilli(zonedDateTime.toInstant().toEpochMilli() + offset * 1000);
        return nowInstant.atZone(ZoneOffset.UTC);
    }
}
