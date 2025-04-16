package com.kartingrm.controller;

import com.kartingrm.entity.Session;
import com.kartingrm.repository.SessionRepository;
import org.springframework.http.ResponseEntity;
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

    // --- MÉTOD0 DELETE AÑADIDO ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable Long id) { // Usar @PathVariable para obtener el ID de la URL
        // Verificar si la sesión existe antes de intentar borrarla
        if (!sessionRepository.existsById(id)) {
            // Si no existe, devolver 404 Not Found
            return ResponseEntity.notFound().build();
        }
        // Si existe, borrarla
        sessionRepository.deleteById(id);
        // Devolver 204 No Content para indicar éxito sin cuerpo de respuesta
        // o 200 OK si prefieres devolver un mensaje
        return ResponseEntity.noContent().build();
        // Alternativa con 200 OK y mensaje:
        // return ResponseEntity.ok().body("Session with ID " + id + " deleted successfully.");
    }
    // --- FIN DEL MÉTOD0 AÑADIDO ---
}
