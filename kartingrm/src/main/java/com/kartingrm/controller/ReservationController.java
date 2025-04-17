package com.kartingrm.controller;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.dto.ReservationResponseDTO;
import com.kartingrm.entity.Reservation;
import com.kartingrm.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }

    @GetMapping
    public List<Reservation> listReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public Reservation getReservation(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> create(
            @Valid @RequestBody ReservationRequestDTO dto) {
        Reservation res = reservationService.createReservation(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationMapper.toDto(res));
    }

}
