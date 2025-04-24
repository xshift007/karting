package com.kartingrm.service;

import com.kartingrm.entity.RateType;
import com.kartingrm.service.pricing.TariffService;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.assertj.core.api.Assertions.*;

class TariffServiceTest {

    @Test
    void forDate_normal_y_finesdeSemana_y_feriado() {
        LocalDate lunes = LocalDate.of(2025,4,21); // lunes
        assertThat(TariffService.forDate(lunes, RateType.LAP_10))
                .isEqualTo(TariffService.LAP_10);

        LocalDate sabado = LocalDate.of(2025,4,19);
        assertThat(TariffService.forDate(sabado, RateType.LAP_10))
                .isEqualTo(TariffService.WEEKEND);

        // 18/09 es feriado según HolidayService
        LocalDate patrias = LocalDate.of(2025,9,18);
        assertThat(TariffService.forDate(patrias, RateType.LAP_10))
                .isEqualTo(TariffService.HOLIDAY);
    }
}
