package com.kartingrm.repository;

import com.kartingrm.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
       SELECT COALESCE(SUM(r.participants),0)
       FROM Reservation r
       WHERE r.session.id = :sessionId
    """)
    int participantsInSession(Long sessionId);

}


