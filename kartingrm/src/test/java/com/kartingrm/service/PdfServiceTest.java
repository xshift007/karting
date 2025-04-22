package com.kartingrm.service;

import com.kartingrm.entity.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.assertj.core.api.Assertions.*;

class PdfServiceTest {

    PdfService pdf = new PdfService();

    @Test
    void buildReceipt_nonEmptyBytes() {
        Client c = new Client(1L, "A", "a@b.c", null,
                LocalDate.of(2000,1,1),0, LocalDateTime.now());
        Session s = new Session(1L, LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusMinutes(30), 5);
        Reservation r = new Reservation(1L, "X1", c, s, 30, 1, RateType.LAP_10,
                15000., 0., 15000., ReservationStatus.PENDING, LocalDateTime.now());
        Payment p = new Payment(1L, r, LocalDateTime.now(), "cash", 19., 2850., 17850.);
        byte[] out = pdf.buildReceipt(r, p);
        assertThat(out).isNotEmpty();
    }
}
