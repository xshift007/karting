package com.kartingrm.controller;

import com.kartingrm.dto.SessionDTO;
import com.kartingrm.entity.Session;
import com.kartingrm.repository.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionRepository sessionRepository;

    public SessionController(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @PostMapping
    public Session createSession(@RequestBody Session session) {
        return sessionRepository.save(session);
    }

    @GetMapping
    public List<Session> getSessions() {
        return sessionRepository.findAll();
    }

    /* ---------- NUEVO: actualizar ---------- */
    @PutMapping("/{id}")
    public Session updateSession(@PathVariable Long id,
                                 @RequestBody Session session) {
        if (!sessionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no existe");
        }
        session.setId(id);
        return sessionRepository.save(session);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        if (!sessionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        sessionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/availability")
    public Map<DayOfWeek, List<SessionDTO>> availability(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        List<Session> sesiones = sessionRepository.findBySessionDateBetween(from, to);
        return sesiones.stream()
                .map(s -> new SessionDTO(
                        s.getId(),
                        s.getSessionDate(),
                        s.getStartTime(),
                        s.getEndTime()))
                .collect(Collectors.groupingBy(
                        dto -> dto.sessionDate().getDayOfWeek(),
                        LinkedHashMap::new,
                        Collectors.toList()));
    }
}
