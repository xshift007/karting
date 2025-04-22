package com.kartingrm.service;

import com.kartingrm.entity.Session;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.repository.ReservationRepository;
import com.kartingrm.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;


@Service
public class SessionService {

    private final SessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;   // NUEVO



    public SessionService(SessionRepository sessionRepo,
                          ReservationRepository reservationRepo) {  // ← nuevo parámetro
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;           // ← lo inicializamos
    }

    public List<Session> weeklyRack(LocalDate monday) {
        return sessionRepo.findBySessionDateBetween(monday, monday.plusDays(6));
    }

    public Session create(Session session) {
        // Solo comprobamos solapamiento al crear una sesión nueva (id == null)
        if (session.getId() == null) {
            boolean overlap = sessionRepo.existsOverlap(
                    session.getSessionDate(),
                    session.getStartTime(),
                    session.getEndTime()
            );
            if (overlap) {
                throw new OverlapException("Ya existe una sesión solapada");
            }
        }
        return sessionRepo.save(session);
    }

    @Transactional
    public void delete(Long id) {
        if (reservationRepo.participantsInSession(id) > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar: la sesión tiene reservas");
        }
        sessionRepo.deleteById(id);
    }
}
