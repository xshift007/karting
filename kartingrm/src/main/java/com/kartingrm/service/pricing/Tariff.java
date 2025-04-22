package com.kartingrm.service.pricing;

import com.kartingrm.entity.RateType;
import com.kartingrm.service.HolidayService;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Getter
public enum Tariff {
    LAP_10(15000, 30),
    LAP_15(20000, 35),
    LAP_20(25000, 40),
    WEEKEND(20000, 35),   // ejemplo: +25% sobre tarifa normal
    HOLIDAY(25000, 40);   // ejemplo: +50% sobre tarifa normal

    private final double price;
    private final int totalMinutes;

    Tariff(double price, int minutes) {
        this.price = price;
        this.totalMinutes = minutes;
    }

    public static Tariff forDate(LocalDate date, RateType rate) {
        // el tipo explícito tiene prioridad
        if (rate == RateType.WEEKEND) return WEEKEND;
        if (rate == RateType.HOLIDAY) return HOLIDAY;

        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) return WEEKEND;
        if (HolidayService.isHoliday(date)) return HOLIDAY;
        return Tariff.valueOf(rate.name());   // LAP_xx
    }

}