package com.kartingrm.service;

import com.kartingrm.dto.PaymentRequestDTO;
import com.kartingrm.dto.ReservationRequestDTO;
import com.kartingrm.entity.*;
import com.kartingrm.exception.OverlapException;
import com.kartingrm.repository.ClientRepository;
import com.kartingrm.repository.SessionRepository;
import com.kartingrm.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceTest {

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private PaymentService paymentService;


    @Autowired PaymentService paySvc;
    @Autowired ReservationService resSvc;
    @Autowired ClientRepository clients;

    @Test
    void payGeneratesVatAndPdf() {
        Client c = clients.save(new Client(null,"Pago","p@e.com",null,
                LocalDate.of(2000,1,1),0,LocalDateTime.now()));
        Reservation r = resSvc.createReservation(
                new ReservationRequestDTO("P1", c.getId(),
                        LocalDate.now().plusDays(1), LocalTime.of(9,0), LocalTime.of(9,30),
                        1, RateType.LAP_10));
        Payment p = paySvc.pay(new PaymentRequestDTO(r.getId(),"cash"));
        assertEquals(r.getFinalPrice()*1.19, p.getFinalAmountInclVat(), 0.01);
    }
}
