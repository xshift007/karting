package com.kartingrm.service;

import com.kartingrm.entity.Reservation;
import com.kartingrm.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Reservation reservation) {
        // Calculo del precio final
        double finalPrice = reservation.getBasePrice() - (reservation.getBasePrice() * reservation.getDiscountPercentage() / 100);
        reservation.setFinalPrice(finalPrice);

        // TODO agregar validaciones adicionales, Recomendaciones :
        // - Verificar que la sesión tenga capacidad disponible
        // - Validar que el cliente esté habilitado para reservar

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }



}
