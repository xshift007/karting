package com.kartingrm.service;

import com.kartingrm.entity.Kart;
import com.kartingrm.entity.KartStatus;
import com.kartingrm.repository.KartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KartService {

    private final KartRepository repo;

    public List<Kart> findAll() { return repo.findAll(); }

    /** Reserva `n` karts disponibles y los marca RESERVED; devuelve los asignados */
    @Transactional
    public List<Kart> allocate(int n) {
        List<Kart> free = repo.findAll()
                .stream()
                .filter(k -> k.getStatus() == KartStatus.AVAILABLE)
                .limit(n)
                .toList();
        if (free.size() < n) {
            throw new IllegalStateException("No hay karts suficientes libres");
        }
        free.forEach(k -> k.setStatus(KartStatus.RESERVED));
        repo.saveAll(free);
        return free;
    }

    @Transactional
    public Kart updateStatus(Long id, KartStatus status) {
        Kart k = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kart no existe"));
        k.setStatus(status);
        return repo.save(k);
    }
}
