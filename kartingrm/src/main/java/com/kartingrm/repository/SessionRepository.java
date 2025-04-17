package com.kartingrm.repository;

import com.kartingrm.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findBySessionDateBetween(LocalDate from, LocalDate to);


    //Consultas por fecha o por intervalo de tiempo si se requieren
    @Query("""
   SELECT CASE WHEN COUNT(s)>0 THEN true ELSE false END
   FROM Session s
   WHERE s.sessionDate = :date
     AND (:start BETWEEN s.startTime AND s.endTime
       OR :end BETWEEN s.startTime AND s.endTime)
""")
    boolean existsOverlap(LocalDate date, LocalTime start, LocalTime end);


}

