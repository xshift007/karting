package com.kartingrm.service.pricing;

import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    /* ---------- % por tamaño de grupo ---------- */
    public double groupDiscount(int participants) {
        return participants <= 2 ? 0 :
                participants <= 5 ? 10 :
                        participants <=10 ? 20 : 30;
    }

    /* ---------- % cliente frecuente (sólo titular) ---------- */
    public double frequentDiscount(int monthlyVisits) {
        return monthlyVisits >= 7 ? 30 :
                monthlyVisits >= 5 ? 20 :
                        monthlyVisits >= 2 ? 10 : 0;
    }

    /* ---------- cantidad de cumpleañeros que obtienen 50 % ---------- */
    public int birthdayWinners(int participants, int birthdayPeople) {
        if (birthdayPeople == 1 && participants >= 3 && participants <= 5)  return 1;
        if (birthdayPeople >= 2 && participants >= 6 && participants <=15) return 2;
        return 0;
    }

    /* ---------- % equivalente del descuento de cumpleaños (compatibilidad) */
    public double birthdayDiscount(int participants, int birthdayPeople) {
        return participants == 0
                ? 0
                : birthdayWinners(participants, birthdayPeople) * 50.0 / participants;
    }
}
