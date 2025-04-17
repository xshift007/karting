package com.kartingrm.service.pricing;

import com.kartingrm.entity.Client;
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

    public double birthdayDiscount(boolean isBirthday, int participants, int birthdayPeople) {
        if (!isBirthday) return 0;
        // 50 % a (1 ó 2) personas según regla
        return (50.0 / participants) * birthdayPeople;
    }
}
