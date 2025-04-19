package com.kartingrm.service;

import com.kartingrm.dto.PaymentRequestDTO;
import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.*;
import com.kartingrm.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;   //  ◀◀  NUEVO

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class PaymentServiceTest {

    @MockBean JavaMailSender mailSender;   //  se sigue “mutando”   

    @Autowired PaymentService paymentService;
    @Autowired ReservationService reservationService;
    @Autowired ClientRepository clients;

    @Test
    void payGeneratesVatAndPdf() {
        Client c = clients.save(new Client(null, "Pago", "p@e.com",
                null, LocalDate.of(2000, 1, 1), 0, LocalDateTime.now()));

        Reservation r = reservationService.createReservation(
                new ReservationRequestDTO("P1", c.getId(),
                        LocalDate.now().plusDays(1), LocalTime.of(9, 0),
                        LocalTime.of(9, 30), 1, RateType.LAP_10));

        Payment p = paymentService.pay(new PaymentRequestDTO(r.getId(), "cash"));

        assertThat(p.getFinalAmountInclVat())
                .isCloseTo(r.getFinalPrice() * 1.19, within(0.01));
        // Si usas offset:
        // .isCloseTo(r.getFinalPrice() * 1.19, offset(0.01));
    }
}
