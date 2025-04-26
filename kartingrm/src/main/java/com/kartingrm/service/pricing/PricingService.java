package com.kartingrm.service.pricing;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.TariffConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final TariffService   tariff;
    private final DiscountService discount;
    private final com.kartingrm.service.ClientService clientSvc;

    public PricingResult calculate(ReservationRequestDTO dto) {

        TariffConfig cfg = tariff.forDate(dto.sessionDate(), dto.rateType());
        double baseUnit  = cfg.getPrice();        // CLP por persona
        int    minutes   = cfg.getMinutes();
        int    people    = dto.participantsList().size();

        /* descuentos ------------------------------------------------------ */
        double dGroup = discount.groupDiscount(people);

        int visitsThisMonth = clientSvc.getTotalVisitsThisMonth(
                clientSvc.get(dto.clientId()));
        double dFreq = discount.frequentDiscount(visitsThisMonth);

        long bdaysCount = dto.participantsList()
                .stream().filter(ReservationRequestDTO.ParticipantDTO::birthday)
                .count();
        double dBdayPct = discount.birthdayDiscount(people, (int) bdaysCount);

        /* aplicación secuencial: grupo → frecuente, luego descuento
           directo de cumpleaños (50 % a 1 – 2 personas) ------------------ */
        double subtotal      = baseUnit * people;                 // precio sin desc.
        double afterGroup    = subtotal * (1 - dGroup/100);
        double afterFreq     = afterGroup * (1 - dFreq/100);
        double bdayAmount    = Math.min(2, bdaysCount) * baseUnit * 0.5;
        double finalPrice    = Math.round(afterFreq - bdayAmount);
        double totalDiscPct  = (subtotal - finalPrice) * 100 / subtotal;

        return new PricingResult(baseUnit, dGroup, dFreq, dBdayPct,
                totalDiscPct, finalPrice, minutes);
    }

    /* -------- resultado interno ---------------------------------------- */
    public record PricingResult(double baseUnit,
                                double discGroupPct,
                                double discFreqPct,
                                double discBirthdayPct,
                                double discTotalPct,
                                double finalPrice,
                                int    minutes) { }
}
