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
import java.time.MonthDay;
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



    // update, cancel, getters…


    public Reservation createReservation(ReservationRequestDTO dto) {

        Client client = clientService.get(dto.clientId());

        Session session = sessionService.create(
                new Session(null, dto.sessionDate(),
                        dto.startTime(), dto.endTime(), 15));
        // --- NUEVO: validar cupo ---
        int ocupados = reservationRepo.participantsInSession(session.getId());
        if (ocupados + dto.participants() > session.getCapacity()) {
            throw new IllegalStateException("Capacidad de la sesión superada");
        }
        //----------------------------
        double base = Tariff.valueOf(dto.rateType().name()).getPrice();

        double dGroup = discService.groupDiscount(dto.participants());
        double dFreq  = discService.frequentDiscount(
                clientService.getTotalVisitsThisMonth(client));
        boolean birthday = MonthDay.from(dto.sessionDate())
                .equals(MonthDay.from(client.getBirthDate()));
        double dBirth = discService.birthdayDiscount(birthday,
                dto.participants(), birthday ? 1 : 0);

        double totalDisc  = dGroup + dFreq + dBirth;
        double finalPrice = base * (1 - totalDisc / 100);

        Reservation res = new Reservation(null,
                dto.reservationCode(), client, session,
                Tariff.valueOf(dto.rateType().name()).getTotalMinutes(),
                dto.participants(), dto.rateType(),
                base, totalDisc, finalPrice,
                ReservationStatus.PENDING, LocalDateTime.now());

        clientService.incrementVisits(client);
        return reservationRepo.save(res);
    }

    public List<Reservation> findAll() {
        return reservationRepo.findAll();
    }

    public Reservation findById(Long id) {
        return reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no existe"));
    }

    public Reservation update(Long id, ReservationRequestDTO dto) {
        Reservation r = findById(id);
        // lógica similar a create: validaciones de cupo, horario, recalcular precios/descuentos…
        // luego:
        r.setParticipants(dto.participants());
        // … resto de campos …
        return reservationRepo.save(r);
    }
    public void save(Reservation r) { reservationRepo.save(r); }



}

