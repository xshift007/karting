package com.kartingrm.service;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.*;
import com.kartingrm.repository.ReservationRepository;
import com.kartingrm.service.pricing.DiscountService;
import com.kartingrm.service.pricing.Tariff;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.MonthDay;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final SessionService sessionService;
    private final ClientService clientService;
    private final DiscountService discService;

    public ReservationService(ReservationRepository r,
                              SessionService s,
                              ClientService c,
                              DiscountService d) {
        this.reservationRepo = r;
        this.sessionService  = s;
        this.clientService   = c;
        this.discService     = d;
    }

    public Reservation createReservation(ReservationRequestDTO dto) {
        Client client = clientService.get(dto.clientId());
        Session session = sessionService.create(
                new Session(null,
                        dto.sessionDate(),
                        dto.startTime(),
                        dto.endTime(),
                        15)
        );

        int ocupados = reservationRepo.participantsInSession(session.getId());
        if (ocupados + dto.participants() > session.getCapacity()) {
            throw new IllegalStateException("Capacidad de la sesión superada");
        }

        double base = Tariff.forDate(dto.sessionDate(), dto.rateType()).getPrice();
        double dGroup = discService.groupDiscount(dto.participants());
        double dFreq  = discService.frequentDiscount(
                clientService.getTotalVisitsThisMonth(client));
        boolean birthday = MonthDay.from(dto.sessionDate())
                .equals(MonthDay.from(client.getBirthDate()));
        double dBirth = discService.birthdayDiscount(
                birthday, dto.participants(), birthday ? 1 : 0);
        double totalDisc = dGroup + dFreq + dBirth;
        double finalPrice = base * (1 - totalDisc / 100);

        Reservation res = new Reservation(
                null,
                dto.reservationCode(),
                client,
                session,
                Tariff.forDate(dto.sessionDate(), dto.rateType()).getTotalMinutes(),
                dto.participants(),
                dto.rateType(),
                base,
                totalDisc,
                finalPrice,
                ReservationStatus.PENDING,
                LocalDateTime.now()
        );
        clientService.incrementVisits(client);
        return reservationRepo.save(res);
    }

    @Transactional
    public Reservation update(Long id, ReservationRequestDTO dto) {

        // 1) Recuperamos la reserva vigente
        Reservation res = findById(id);

        // 2) Actualizamos código (si cambió)
        res.setReservationCode(dto.reservationCode());

        // 3) Ajustamos la sesión **existente** (no se crea otra)
        Session ses = res.getSession();
        ses.setSessionDate(dto.sessionDate());
        ses.setStartTime(dto.startTime());
        ses.setEndTime(dto.endTime());
        // ‑ CascadeType.ALL se encargará de persistir los cambios en la sesión

        // 4) Chequeo de capacidad: contamos ocupados excepto la propia reserva
        int ocupados = reservationRepo.participantsInSession(ses.getId()) - res.getParticipants();
        if (ocupados + dto.participants() > ses.getCapacity()) {
            throw new IllegalStateException("Capacidad de la sesión superada");
        }
        res.setParticipants(dto.participants());

        // 5) Re‑calculo de tarifas y descuentos
        double base = Tariff.forDate(dto.sessionDate(), dto.rateType()).getPrice();
        res.setBasePrice(base);

        double dGroup = discService.groupDiscount(dto.participants());
        double dFreq  = discService.frequentDiscount(
                clientService.getTotalVisitsThisMonth(res.getClient()));

        boolean birthday = MonthDay.from(dto.sessionDate())
                .equals(MonthDay.from(res.getClient().getBirthDate()));
        double dBirth = discService.birthdayDiscount(
                birthday, dto.participants(), birthday ? 1 : 0);

        double totalDisc = dGroup + dFreq + dBirth;
        res.setDiscountPercentage(totalDisc);
        res.setFinalPrice(base * (1 - totalDisc / 100));

        // 6) Guardamos y devolvemos la reserva actualizada
        return reservationRepo.save(res);
    }


    public List<Reservation> findAll() {
        return reservationRepo.findAll();
    }

    public Reservation findById(Long id) {
        return reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no existe"));
    }

    public void save(Reservation r) {
        reservationRepo.save(r);
    }
}
