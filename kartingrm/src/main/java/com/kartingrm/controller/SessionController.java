package com.kartingrm.controller;

import com.kartingrm.entity.Session;
import com.kartingrm.repository.SessionRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}
