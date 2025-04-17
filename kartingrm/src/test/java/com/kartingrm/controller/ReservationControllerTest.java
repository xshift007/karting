package com.kartingrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kartingrm.dto.ClientDTO;
import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.dto.ReservationResponseDTO;
import com.kartingrm.dto.SessionDTO;
import com.kartingrm.entity.Reservation;
import com.kartingrm.entity.RateType;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.mapper.ReservationMapper;
import com.kartingrm.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
class ReservationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ReservationMapper reservationMapper;

    @Test
    void postValidReservation() throws Exception {
        // Arrange: construir petición y respuesta esperada
        ReservationRequestDTO req = new ReservationRequestDTO(
                "C1",
                1L,
                LocalDate.of(2025, 4, 17),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                2,
                RateType.LAP_10
        );

        // Simular entidad guardada
        Reservation saved = new Reservation();
        saved.setId(1L);
        saved.setReservationCode("C1");
        // no necesitamos rellenar más campos, el mapper es el que genera el DTO

        // Simular DTO de respuesta
        ReservationResponseDTO dto = new ReservationResponseDTO(
                1L,
                "C1",
                new ClientDTO(1L, "Test User", "test@example.com"),
                new SessionDTO(1L, LocalDate.of(2025, 4, 17), LocalTime.of(10, 0), LocalTime.of(10, 30)),
                2,
                RateType.LAP_10,
                15000.0,
                0.0,
                15000.0,
                null // o ReservationStatus.PENDING si quieres comprobar más
        );

        when(reservationService.createReservation(req)).thenReturn(saved);
        when(reservationMapper.toDto(saved)).thenReturn(dto);

        // Act & Assert: realizar POST y verificar JSON
        mvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationCode").value("C1"));
    }
}
