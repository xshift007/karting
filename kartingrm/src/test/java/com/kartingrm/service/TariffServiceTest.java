package com.kartingrm.service;

import com.kartingrm.entity.*;
import com.kartingrm.service.pricing.TariffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class TariffServiceTest {

    @Autowired TariffService svc;

    @Test
    void forDate_normal_weekend_holiday() {

        LocalDate monday   = LocalDate.of(2025,4,21);
        TariffConfig cfg1  = svc.forDate(monday, RateType.LAP_10);
        assertThat(cfg1.getRate()).isEqualTo(RateType.LAP_10);

        LocalDate saturday = LocalDate.of(2025,4,19);
        TariffConfig cfg2  = svc.forDate(saturday, RateType.LAP_10);
        assertThat(cfg2.getRate()).isEqualTo(RateType.WEEKEND);

        LocalDate patrias  = LocalDate.of(2025,9,18);           // feriado
        TariffConfig cfg3  = svc.forDate(patrias, RateType.LAP_10);
        assertThat(cfg3.getRate()).isEqualTo(RateType.HOLIDAY);
    }
}
