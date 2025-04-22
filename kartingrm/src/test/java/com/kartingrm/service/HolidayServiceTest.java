package com.kartingrm.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class HolidayServiceTest {

    @Test
    void isHoliday_fixed() {
        assertThat(HolidayService.isHoliday(LocalDate.of(2025,9,18))).isTrue();
        assertThat(HolidayService.isHoliday(LocalDate.of(2025,12,25))).isTrue();
        assertThat(HolidayService.isHoliday(LocalDate.of(2025,1,1))).isFalse();
    }
}
