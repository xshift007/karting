package com.kartingrm.service.pricing;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.TariffConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final TariffService  tariff;
    private final DiscountService discount;
    private final com.kartingrm.service.ClientService clientSvc;

    public PricingResult calculate(ReservationRequestDTO dto) {

        TariffConfig cfg = tariff.forDate(dto.sessionDate(), dto.rateType());
        double baseUnit  = cfg.getPrice();                       // precio por persona
        int    people    = dto.participantsList().size();

        double dGroup = discount.groupDiscount(people);
        int    visits = clientSvc
                .getTotalVisitsThisMonth(clientSvc.get(dto.clientId()));
        double dFreq  = discount.frequentDiscount(visits);

        long   bdays  = dto.participantsList()
                .stream().filter(ReservationRequestDTO.ParticipantDTO::birthday)
                .count();
        double dBday = discount.birthdayDiscount(bdays > 0, people, (int) bdays);

        double totalDiscPct = dGroup + dFreq + dBday;
        double finalPrice   = Math.round(baseUnit * people *
                (1 - totalDiscPct / 100));

        return new PricingResult(baseUnit, dGroup, dFreq, dBday,
                totalDiscPct, finalPrice);
    }

    /* ------------ DTO interno ------------ */
    public record PricingResult(double baseUnit,
                                double discGroup,
                                double discFreq,
                                double discBirthday,
                                double discTotalPct,
                                double finalPrice) { }
}
