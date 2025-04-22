package com.kartingrm.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Set;


@Service
public class HolidayService {
    private static final Set<MonthDay> FIXED_HOLIDAYS = Set.of(
            MonthDay.of(9, 18),   // fiestas patrias
            MonthDay.of(12, 25)   // navidad
            // … otros fijos
    );

    public static boolean isHoliday(LocalDate d) {
        return FIXED_HOLIDAYS.contains(MonthDay.from(d));
    }
}