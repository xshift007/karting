package com.kartingrm.service;

import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.Client;
import com.kartingrm.entity.RateType;
import com.kartingrm.entity.Reservation;
import com.kartingrm.entity.Session;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.repository.ClientRepository;
import com.kartingrm.repository.SessionRepository;
import com.kartingrm.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationServiceTest {

    @Autowired ReservationService service;
    @Autowired
    ClientRepository clients;

    @BeforeEach
    void setup() {
        clients.deleteAll();
        // crear cliente con 6 visitas
        Client c = new Client(null, "Test", "t@t.com", null, LocalDate.now(), 6, LocalDateTime.now());
        clients.save(c);
    }

    @Test
    void calculateDiscounts() {
        Client c = clients.findAll().get(0);
        var dto = new ReservationRequestDTO(
                "R1", c.getId(), LocalDate.now(), LocalTime.of(12,0), LocalTime.of(12,30), 4, RateType.LAP_10);
        Reservation res = service.createReservation(dto);
        // grupo (10%) + frecuente (20%) = 30% total
        assertEquals(15000 * 0.70, res.getFinalPrice());
    }
}