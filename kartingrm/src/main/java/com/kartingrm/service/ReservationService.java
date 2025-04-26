package com.kartingrm.service;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.dto.ReservationRequestDTO.ParticipantDTO;
import com.kartingrm.entity.*;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.repository.*;
import com.kartingrm.service.pricing.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;                 // + añadir
import org.springframework.web.server.ResponseStatusException; // + añadir

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repo;
    private final ClientService         clients;
    private final SessionRepository     sessions;
    private final PricingService        pricing;

    /* --------------------------------------------------------------------- */
    @Transactional
    public Reservation createReservation(ReservationRequestDTO dto) {

        Session s = findSessionOrThrow(dto.sessionDate(),
                dto.startTime(), dto.endTime());

        int already = repo.participantsInSession(s.getId());
        int requested = dto.participantsList().size();
        if (already + requested > s.getCapacity())
            throw new IllegalStateException("Capacidad de la sesión superada");

        PricingService.PricingResult pr = pricing.calculate(dto);

        Reservation r = buildEntity(dto, s, pr);
        return repo.save(r);
    }

    /* --------------------------------------------------------------------- */
    @Transactional
    public Reservation update(Long id, ReservationRequestDTO dto) {

        Reservation existing = findById(id);

        // se prohíbe cambiar de bloque; solo se admite variar participantes
        if (!existing.getSession().getSessionDate().equals(dto.sessionDate()) ||
                !existing.getSession().getStartTime()   .equals(dto.startTime())   ||
                !existing.getSession().getEndTime()     .equals(dto.endTime()))
            throw new IllegalStateException("No se puede cambiar el bloque; cree otra reserva");

        Session s = existing.getSession();
        int already = repo.participantsInSession(s.getId()) - existing.getParticipants();
        int requested = dto.participantsList().size();
        if (already + requested > s.getCapacity())
            throw new IllegalStateException("Capacidad de la sesión superada");

        PricingService.PricingResult pr = pricing.calculate(dto);

        /* actualiza campos */
        existing.setParticipants(requested);
        existing.setBasePrice(pr.baseUnit());
        existing.setDiscountPercentage(pr.discTotalPct());
        existing.setFinalPrice(pr.finalPrice());
        existing.getParticipantsList().clear();
        existing.getParticipantsList()
                .addAll(toEntities(dto.participantsList(), existing));

        return repo.save(existing);
    }

    /* --------------------------------------------------------------------- */
    public List<Reservation> findAll()        { return repo.findAll(); }
    public Reservation       findById(Long id){ return repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no existe")); }
    public void              save(Reservation r){ repo.save(r); }

    /* ---------- helpers privados ----------------------------------------- */
    private Session findSessionOrThrow(LocalDate d, LocalTime s, LocalTime e){
        return sessions
                .findBySessionDateAndStartTimeAndEndTime(d, s, e)
                .orElseThrow(() ->
                        /* AHORA DEVUELVE 404 (NOT_FOUND) ------------------- */
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La sesión solicitada no existe; "
                                        + "debe crearla el administrador"));
    }

    private Reservation buildEntity(ReservationRequestDTO dto,
                                    Session s,
                                    PricingService.PricingResult pr){

        Client c = clients.get(dto.clientId());

        Reservation r = new Reservation();
        r.setReservationCode(dto.reservationCode());
        r.setClient(c);
        r.setSession(s);
        r.setDuration(s.getCapacity());                    // minutos totales (misma lógica)
        r.setParticipants(dto.participantsList().size());
        r.setRateType(dto.rateType());
        r.setBasePrice(pr.baseUnit());
        r.setDiscountPercentage(pr.discTotalPct());
        r.setFinalPrice(pr.finalPrice());
        r.setParticipantsList(toEntities(dto.participantsList(), r));
        return r;
    }

    private List<Participant> toEntities(List<ParticipantDTO> list, Reservation r){
        return list.stream().map(p ->
                new Participant(null, p.fullName(), p.email(), p.birthday(), r)
        ).toList();
    }


}
