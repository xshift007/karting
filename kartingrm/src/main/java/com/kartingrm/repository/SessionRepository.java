package com.kartingrm.repository;

import com.kartingrm.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    //Consultas por fecha o por intervalo de tiempo si se requieren
}
