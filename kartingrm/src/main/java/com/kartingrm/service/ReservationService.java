package com.kartingrm.service;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.Client;
import com.kartingrm.entity.Reservation;
import com.kartingrm.entity.ReservationStatus;
import com.kartingrm.entity.Session;
import com.kartingrm.repository.ReservationRepository;
import com.kartingrm.service.pricing.DiscountService;
import com.kartingrm.service.pricing.Tariff;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service @Transactional
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final SessionService sessionService;
    private final ClientService clientService;
    private final DiscountService discService;

    public ReservationService(ReservationRepository r, SessionService s,
                              ClientService c, DiscountService d) {
        this.reservationRepo = r;
        this.sessionService = s;
        this.clientService  = c;
        this.discService    = d;
    }

    public Reservation createReservation(ReservationRequestDTO dto) {

        Client client = clientService.get(dto.clientId());
        // 1.  bloque / sesión
        Session session = sessionService.create(
                new Session(null, dto.sessionDate(),
                        dto.startTime(), dto.endTime(), 15));

        // 2. precio base
        double base = Tariff.valueOf(dto.rateType().name()).getPrice();

        // 3. descuentos
        double dGroup   = discService.groupDiscount(dto.participants());
        double dFreq    = discService.frequentDiscount(client.getTotalVisitsThisMonth());
        boolean birthday = dto.sessionDate().equals(client.getBirthDate());
        double dBirth   = discService.birthdayDiscount(birthday,
                dto.participants(), birthday ? 1 : 0);

        double totalDisc = dGroup + dFreq + dBirth;
        double finalPrice = base * (1 - totalDisc / 100);

        Reservation res = new Reservation(null,
                dto.reservationCode(), client, session,
                Tariff.valueOf(dto.rateType().name()).getTotalMinutes(),
                dto.participants(), dto.rateType(),
                base, totalDisc, finalPrice,
                ReservationStatus.PENDING, LocalDateTime.now());

        // 4. persistir y actualizar visitas
        clientService.incrementVisits(client);
        return reservationRepo.save(res);
    }

    // update, cancel, getters…
}

