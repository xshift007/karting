package com.kartingrm.controller;
import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.Client;
import com.kartingrm.entity.RateType;
import com.kartingrm.entity.Reservation;
import com.kartingrm.entity.Session;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.mapper.ReservationMapper;
import com.kartingrm.repository.ClientRepository;
import com.kartingrm.repository.SessionRepository;
import com.kartingrm.service.ReservationService;
import com.kartingrm.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;



@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    ReservationService service;
    @MockBean
    ReservationMapper mapper;

    @Test
    void postValidReservation() throws Exception {
        var req = new ReservationRequestDTO("C1", 1L, LocalDate.now(), LocalTime.of(10,0), LocalTime.of(10,30), 2, RateType.LAP_10);
        var res = new Reservation(...); // instancia
        when(service.createReservation(req)).thenReturn(res);
        when(mapper.toDto(res)).thenReturn(new ReservationResponseDTO(...));

        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationCode").value("C1"));
    }
}