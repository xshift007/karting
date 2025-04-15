package com.kartingrm.repository;

import com.kartingrm.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //Métodos adicionales de búsqueda: por código de reserva o por sesión

}


