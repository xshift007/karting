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
        DayOfWeek dow = date.getDayOfWeek();
        boolean isWeekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
        if (isWeekend) return WEEKEND;
        if (HolidayService.isHoliday(date)) return HOLIDAY;
        // si no, uso tarifa normal según rate
        return Tariff.valueOf(rate.name());
    }
}