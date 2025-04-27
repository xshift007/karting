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

        /* -------- datos base -------- */
        TariffConfig cfg = tariff.forDate(dto.sessionDate(), dto.rateType());
        double baseUnit  = cfg.getPrice();
        int    minutes   = cfg.getMinutes();
        int    people    = dto.participantsList().size();
        double subtotal  = baseUnit * people;

        /* -------- descuentos % -------- */
        double dGroup = discount.groupDiscount(people);

        int visitsThisMonth = clientSvc.getTotalVisitsThisMonth(
                clientSvc.get(dto.clientId()));
        double dFreq = discount.frequentDiscount(visitsThisMonth);

        long bdays = dto.participantsList().stream()
                .filter(ReservationRequestDTO.ParticipantDTO::birthday)
                .count();
        double dBdayPct = discount.birthdayDiscount(people, (int) bdays);

        /* -------- aplicación de descuentos -------- */
        double afterPct = subtotal * (1 - (dGroup + dFreq + dBdayPct) / 100);

        /* final redondeado al peso */
        double finalPrice   = Math.round(afterPct);
        double totalDiscPct = (subtotal - finalPrice) * 100 / subtotal;

        return new PricingResult(
                baseUnit, dGroup, dFreq, dBdayPct,
                totalDiscPct, finalPrice, minutes
        );
    }

    /* ---------- resultado ---------- */
    public record PricingResult(
            double baseUnit,
            double discGroupPct,
            double discFreqPct,
            double discBirthdayPct,
            double discTotalPct,
            double finalPrice,
            int    minutes) { }
}
