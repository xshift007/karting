package com.kartingrm.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservations")
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="reservation_code", nullable=false, unique=true)
    private String reservationCode;

    @ManyToOne
    @JoinColumn(name="client_id", nullable=false)
    private Client client;

    @OneToOne
    @JoinColumn(name="session_id", nullable=false)
    private Session session;

    // Duración de la reserva en minutos (generalmente igual a la duración de la sesión)
    @Column(nullable=false)
    private Integer duration;

    // Número de participantes en la reserva (para aplicar descuentos por grupo)
    @Column(nullable=false)
    private Integer participants;

    @Enumerated(EnumType.STRING)
    @Column(name="rate_type", nullable=false)
    private RateType rateType; // LAP_10, LAP_15, LAP_20, WEEKEND, HOLIDAY, BIRTHDAY

    @Column(name="base_price", nullable=false)
    private Double basePrice;

    // Descuento global calculado sobre el basePrice
    @Column(name="discount_percentage")
    private Double discountPercentage = 0.0;

    @Column(name="final_price", nullable=false)
    private Double finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
