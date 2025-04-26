package com.kartingrm.service.pricing;

import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    public double groupDiscount(int participants) {
        return participants <= 2 ? 0 :
                participants <= 5 ? 10 :
                        participants <=10 ? 20 : 30;
    }

    public double frequentDiscount(int monthlyVisits) {
        return monthlyVisits >= 7 ? 30 :
                monthlyVisits >= 5 ? 20 :
                        monthlyVisits >= 2 ? 10 : 0;
    }

    /** 50 % al precio base de, como máximo, dos cumpleañeros. */
    public double birthdayDiscount(int participants, int birthdayPeople) {
        if (birthdayPeople == 0) return 0;
        int applied = Math.min(2, birthdayPeople);
        return (50.0 * applied) / participants;
    }
}
