package com.kartingrm.service;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.*;
import com.kartingrm.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReservationServiceTest {

    @Autowired ReservationService service;
    @Autowired ClientRepository clients;

    @BeforeEach
    void setup() {
        clients.deleteAll();
        clients.save(new Client(null, "Test", "t@t.com",
                null, LocalDate.of(2000, 1, 1), 6, LocalDateTime.now()));
    }

    @Test
    void calculateCombinedDiscountsCorrectly() {
        Client c = clients.findAll().get(0);

        ReservationRequestDTO dto = new ReservationRequestDTO(
                "R1", c.getId(),
                LocalDate.of(2030, 4, 18),
                LocalTime.of(12, 0),
                LocalTime.of(12, 30),
                4, RateType.LAP_10);

        Reservation res = service.createReservation(dto);

        assertThat(res.getFinalPrice()).isEqualTo(15000 * 0.70);
    }
}
