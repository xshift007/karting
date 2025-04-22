package com.kartingrm.service;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.Client;
import com.kartingrm.entity.RateType;
import com.kartingrm.entity.Reservation;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationServiceTest {

    @Autowired
    private ReservationService svc;

    @Autowired
    private ClientRepository clients;

    private Client c;

    @BeforeEach
    void setup() {
        clients.deleteAll();
        c = clients.save(new Client(
                null,
                "Test",
                "t@t.com",
                null,
                LocalDate.of(2000, 1, 1),
                0,
                LocalDateTime.now()
        ));
    }

    @Test
    void create_and_update_success() {
        // Creamos la reserva para mañana de 15:00 a 15:30 con 2 participantes
        var dto1 = new ReservationRequestDTO(
                "R1",
                c.getId(),
                LocalDate.now().plusDays(1),
                LocalTime.of(15, 0),
                LocalTime.of(15, 30),
                2,
                RateType.LAP_10
        );
        Reservation r = svc.createReservation(dto1);
        assertThat(r.getParticipants()).isEqualTo(2);

        // Actualizamos a 3 participantes en la misma sesión
        var dto2 = new ReservationRequestDTO(
                "R1",
                c.getId(),
                r.getSession().getSessionDate(),
                r.getSession().getStartTime(),
                r.getSession().getEndTime(),
                3,
                RateType.LAP_10
        );
        Reservation updated = svc.update(r.getId(), dto2);
        assertThat(updated.getParticipants()).isEqualTo(3);
    }

    @Test
    void update_capacityExceeded_throws() {
        // Creamos la reserva para mañana de 15:00 a 15:30 con la máxima capacidad (15)
        var dto1 = new ReservationRequestDTO(
                "R2",
                c.getId(),
                LocalDate.now().plusDays(1),
                LocalTime.of(15, 0),
                LocalTime.of(15, 30),
                15,
                RateType.LAP_10
        );
        Reservation r = svc.createReservation(dto1);

        // Intentamos actualizar a 16 participantes -> debería lanzar IllegalStateException
        var dto2 = new ReservationRequestDTO(
                "R2",
                c.getId(),
                r.getSession().getSessionDate(),
                r.getSession().getStartTime(),
                r.getSession().getEndTime(),
                16,
                RateType.LAP_10
        );
        assertThatThrownBy(() -> svc.update(r.getId(), dto2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Capacidad de la sesión superada");
    }
}
