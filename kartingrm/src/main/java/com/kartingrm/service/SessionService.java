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

    public Session create(Session s) {
        LocalTime start = s.getStartTime(), end = s.getEndTime();
        DayOfWeek dow = s.getSessionDate().getDayOfWeek();

        LocalTime open = (dow == SATURDAY || dow == SUNDAY || HolidayService.isHoliday(s.getSessionDate()))
                ? LocalTime.of(10,0)
                : LocalTime.of(14,0);
        LocalTime close = LocalTime.of(22,0);

        if (start.isBefore(open) || end.isAfter(close)) {
            throw new IllegalArgumentException(
                    "Horario fuera de atención: " + open + "–" + close);
        }

        if (sessionRepo.existsOverlap(s.getSessionDate(), start, end)) {
            throw new OverlapException("Ya existe una sesión solapada");
        }
        return sessionRepo.save(s);

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
