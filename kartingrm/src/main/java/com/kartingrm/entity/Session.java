package com.kartingrm.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "sessions")
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="session_date", nullable=false)
    private LocalDate sessionDate;

    @Column(name="start_time", nullable=false)
    private LocalTime startTime;

    @Column(name="end_time", nullable=false)
    private LocalTime endTime;

    @Column(name="capacity", nullable=false)
    private Integer capacity;  // Total de karts disponibles para este bloque

    // Session.java  (añadir la relación inversa)
    @OneToMany(mappedBy = "session", cascade = CascadeType.REMOVE)
    private List<Reservation> reservations = new ArrayList<>();

}
