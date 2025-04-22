package com.kartingrm.controller;

import com.kartingrm.dto.SessionDTO;
import com.kartingrm.entity.Session;
import com.kartingrm.repository.SessionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
