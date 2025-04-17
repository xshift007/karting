package com.kartingrm.service;

import com.kartingrm.entity.Session;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepo;

    public SessionService(SessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    public List<Session> weeklyRack(LocalDate monday) {
        return sessionRepo.findBySessionDateBetween(monday, monday.plusDays(6));
    }

    @Transactional
    public Session create(Session s) {
        if (sessionRepo.existsOverlap(
                s.getSessionDate(), s.getStartTime(), s.getEndTime())) {
            throw new OverlapException("La pista ya está ocupada en ese bloque");
        }
        return sessionRepo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        // verificar reservas antes de borrar
        sessionRepo.deleteById(id);
    }
}
