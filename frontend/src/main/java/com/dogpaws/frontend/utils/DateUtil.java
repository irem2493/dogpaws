package com.dogpaws.frontend.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 주어진 마감일(deadlineDate)을 기준으로 현재 날짜와의 차이를 계산하여 "D-0", "D-1" 형식으로 반환
     */
    public static String calculateDaysLeft(String deadlineDate) {
        if (deadlineDate == null || deadlineDate.isEmpty()) {
            return null;
        }

        try {
            LocalDate deadline = LocalDate.parse(deadlineDate, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            long daysLeft = ChronoUnit.DAYS.between(today, deadline);

            if (daysLeft < 0) {
                return "D+" + Math.abs(daysLeft);
            } else {
                return "D-" + daysLeft;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다: " + deadlineDate, e);
        }
    }
}
