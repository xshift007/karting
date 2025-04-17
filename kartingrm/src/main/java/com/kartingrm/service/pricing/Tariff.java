package com.kartingrm.service.pricing;

import lombok.Getter;

@Getter
public enum Tariff {
    LAP_10(15000, 30),
    LAP_15(20000, 35),
    LAP_20(25000, 40);

    private final double price;
    private final int totalMinutes;

    Tariff(double price, int minutes) {
        this.price = price;
        this.totalMinutes = minutes;
    }
}
