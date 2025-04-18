package com.kartingrm.service;

import com.kartingrm.entity.Session;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.repository.ReservationRepository;
import com.kartingrm.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class SessionServiceTest {

    @Autowired
    SessionService service;
    @Autowired
    SessionRepository repo;

    @Autowired
    ReservationRepository reservations;

    @BeforeEach
    void clean() {
        reservations.deleteAll(); // primero reservas – rompe la FK
        repo.deleteAll();         // luego sesiones
    }

    @Test
    void createWithoutOverlap() {
        Session s1 = new Session(null, LocalDate.now(), LocalTime.of(10,0), LocalTime.of(11,0), 5);
        assertDoesNotThrow(() -> service.create(s1));
    }

    @Test
    void createWithOverlap() {
        Session s1 = new Session(null, LocalDate.now(), LocalTime.of(9,0), LocalTime.of(10,0), 5);
        service.create(s1);
        Session s2 = new Session(null, LocalDate.now(), LocalTime.of(9,30), LocalTime.of(10,30), 5);
        assertThrows(OverlapException.class, () -> service.create(s2));
    }
}