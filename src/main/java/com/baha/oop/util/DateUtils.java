package com.baha.oop.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static long getDaysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
}