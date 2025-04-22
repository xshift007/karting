package com.kartingrm.service;

import com.kartingrm.entity.RateType;
import com.kartingrm.service.pricing.Tariff;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.assertj.core.api.Assertions.*;

class TariffTest {

    @Test
    void forDate_normal_y_finesdeSemana_y_feriado() {
        LocalDate lunes = LocalDate.of(2025,4,21); // lunes
        assertThat(Tariff.forDate(lunes, RateType.LAP_10))
                .isEqualTo(Tariff.LAP_10);

        LocalDate sabado = LocalDate.of(2025,4,19);
        assertThat(Tariff.forDate(sabado, RateType.LAP_10))
                .isEqualTo(Tariff.WEEKEND);

        // 18/09 es feriado según HolidayService
        LocalDate patrias = LocalDate.of(2025,9,18);
        assertThat(Tariff.forDate(patrias, RateType.LAP_10))
                .isEqualTo(Tariff.HOLIDAY);
    }
}
