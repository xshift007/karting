package com.kartingrm.service.pricing;

import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    /* ------------ % por tamaño de grupo ------------ */
    public double groupDiscount(int participants) {
        return participants <= 2 ? 0 :
                participants <= 5 ? 10 :
                        participants <=10 ? 20 : 30;
    }

    /* ------------ % por cliente frecuente ------------ */
    public double frequentDiscount(int monthlyVisits) {
        return monthlyVisits >= 7 ? 30 :
                monthlyVisits >= 5 ? 20 :
                        monthlyVisits >= 2 ? 10 : 0;
    }

    /**
     * % equivalente del descuento de cumpleaños.
     *
     * - 1 cumpleañero: 50 % a 1 persona si el grupo es 3-5
     * - 2+ cumpleañeros: 50 % a 2 personas si el grupo es 6-15
     *
     * Devuelve 0 si no se cumplen las condiciones.
     */
    public double birthdayDiscount(int participants, int birthdayPeople) {

        if (birthdayPeople == 1 && participants >= 3 && participants <= 5) {
            return 50.0 / participants;                // ½ precio a 1 persona
        }

        if (birthdayPeople >= 2 && participants >= 6 && participants <= 15) {
            return 100.0 / participants;               // ½ precio a 2 personas
        }

        return 0.0;
    }
}
