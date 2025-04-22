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

    public Reservation update(Long id, ReservationRequestDTO dto) {
        Reservation existing = findById(id);
        existing.setReservationCode(dto.reservationCode());

        // recrear la sesión con la misma capacidad
        Session oldSes = existing.getSession();
        Session newSes = sessionService.create(
                new Session(oldSes.getId(),
                        dto.sessionDate(),
                        dto.startTime(),
                        dto.endTime(),
                        oldSes.getCapacity())
        );
        existing.setSession(newSes);

        // ajustar el cupo (quitamos nuestros propios participantes del recuento)
        int ocupados = reservationRepo.participantsInSession(newSes.getId())
                - existing.getParticipants();
        if (ocupados + dto.participants() > newSes.getCapacity()) {
            throw new IllegalStateException("Capacidad de la sesión superada");
        }
        existing.setParticipants(dto.participants());

        // recalcular precios y descuentos (idéntico a create)
        double base = Tariff.forDate(dto.sessionDate(), dto.rateType()).getPrice();
        existing.setBasePrice(base);
        double dGroup = discService.groupDiscount(dto.participants());
        double dFreq  = discService.frequentDiscount(
                clientService.getTotalVisitsThisMonth(existing.getClient()));
        boolean birthday = MonthDay.from(dto.sessionDate())
                .equals(MonthDay.from(existing.getClient().getBirthDate()));
        double dBirth = discService.birthdayDiscount(
                birthday, dto.participants(), birthday ? 1 : 0);
        double totalDisc = dGroup + dFreq + dBirth;
        existing.setDiscountPercentage(totalDisc);
        existing.setFinalPrice(base * (1 - totalDisc / 100));

        return reservationRepo.save(existing);
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
