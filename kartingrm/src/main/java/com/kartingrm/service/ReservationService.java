package com.kartingrm.service;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.dto.ReservationRequestDTO.ParticipantDTO;
import com.kartingrm.entity.*;
import com.kartingrm.repository.ReservationRepository;
import com.kartingrm.service.pricing.DiscountService;
import com.kartingrm.service.pricing.TariffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.MonthDay;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final SessionService        sessionService;
    private final ClientService         clientService;
    private final DiscountService       discService;
    private final TariffService         tariffService;

    public Reservation createReservation(ReservationRequestDTO dto) {

        Client client = clientService.get(dto.clientId());

        // 1) Crear la sesión (valida solapamiento internamente)
        Session session = sessionService.create(
                new Session(null, dto.sessionDate(),
                        dto.startTime(), dto.endTime(), 15));

        // 2) Capacidad
        int ocupados = reservationRepo.participantsInSession(session.getId());
        int totalPersons = dto.participantsList().size();
        if (ocupados + totalPersons > session.getCapacity())
            throw new IllegalStateException("Capacidad de la sesión superada");

        // 3) Tarifa base y minutos
        TariffConfig cfg = tariffService.forDate(dto.sessionDate(), dto.rateType());
        double base  = cfg.getPrice();
        int minutes  = cfg.getMinutes();

        // 4) Descuentos
        double dGroup = discService.groupDiscount(totalPersons);
        double dFreq  = discService.frequentDiscount(
                clientService.getTotalVisitsThisMonth(client));
        int birthdayPeople = (int) dto.participantsList().stream()
                .filter(ParticipantDTO::birthday).count();
        int maxAllowed = totalPersons<=5?1 : totalPersons<=10?2 : 0;
        birthdayPeople = Math.min(birthdayPeople, maxAllowed);

        double dBirth = discService.birthdayDiscount(
                birthdayPeople>0, totalPersons, birthdayPeople);

        double totalDisc  = dGroup + dFreq + dBirth;
        double finalPrice = base * (1 - totalDisc/100);

        // 5) Mapping ParticipantsDTO → entidad
        List<Participant> entities =
                dto.participantsList().stream().map(p -> {
                    Participant e = new Participant();
                    e.setFullName(p.fullName());
                    e.setEmail(p.email());
                    e.setBirthday(p.birthday());
                    return e;
                }).collect(Collectors.toList());

        // 6) Construir reserva
        Reservation res = new Reservation();
        res.setReservationCode(dto.reservationCode());
        res.setClient(client);
        res.setSession(session);
        res.setDuration(minutes);
        res.setParticipants(totalPersons);
        res.setRateType(cfg.getRate());
        res.setBasePrice(base);
        res.setDiscountPercentage(totalDisc);
        res.setFinalPrice(finalPrice);
        res.getParticipantsList().addAll(entities);
        entities.forEach(p -> p.setReservation(res));      // relación inversa

        clientService.incrementVisits(client);

        return reservationRepo.save(res);
    }

    /* ---------- MÉTODO update (igual a version create pero sobre entidad existente) -------- */
    public Reservation update(Long id, ReservationRequestDTO dto) {

        Reservation res = findById(id);

        // actualizar código
        res.setReservationCode(dto.reservationCode());

        // actualizar sesión existente
        Session ses = res.getSession();
        ses.setSessionDate(dto.sessionDate());
        ses.setStartTime(dto.startTime());
        ses.setEndTime(dto.endTime());

        // chequeo de capacidad
        int ocupados = reservationRepo.participantsInSession(ses.getId())
                - res.getParticipants();
        int totalPersons = dto.participantsList().size();
        if (ocupados + totalPersons > ses.getCapacity())
            throw new IllegalStateException("Capacidad de la sesión superada");

        // tarifa y minutos
        TariffConfig cfg = tariffService.forDate(dto.sessionDate(), dto.rateType());
        double base = cfg.getPrice();
        int minutes = cfg.getMinutes();

        // descuentos
        double dGroup = discService.groupDiscount(totalPersons);
        double dFreq  = discService.frequentDiscount(
                clientService.getTotalVisitsThisMonth(res.getClient()));
        int birthdayPeople = (int) dto.participantsList().stream()
                .filter(ParticipantDTO::birthday).count();
        int maxAllowed = totalPersons<=5?1 : totalPersons<=10?2 : 0;
        birthdayPeople = Math.min(birthdayPeople, maxAllowed);
        double dBirth = discService.birthdayDiscount(
                birthdayPeople>0, totalPersons, birthdayPeople);

        double totalDisc = dGroup + dFreq + dBirth;

        // aplicar cambios
        res.setParticipants(totalPersons);
        res.setDuration(minutes);
        res.setRateType(cfg.getRate());
        res.setBasePrice(base);
        res.setDiscountPercentage(totalDisc);
        res.setFinalPrice(base * (1 - totalDisc/100));

        // reemplazar lista de participantes
        res.getParticipantsList().clear();
        List<Participant> entities = dto.participantsList().stream().map(p -> {
            Participant e = new Participant(null, p.fullName(), p.email(),
                    p.birthday(), res);
            return e;
        }).toList();
        res.getParticipantsList().addAll(entities);

        return reservationRepo.save(res);
    }

    /* ---------------- auxiliares ---------------- */
    public List<Reservation> findAll(){ return reservationRepo.findAll(); }
    public Reservation findById(Long id){
        return reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no existe"));
    }
    public void save(Reservation r){ reservationRepo.save(r); }
}
