package com.kartingrm.service;

import com.kartingrm.service.pricing.DiscountService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DiscountServiceTest {

    DiscountService svc = new DiscountService();

    @Test
    void groupDiscount() {
        assertThat(svc.groupDiscount(1)).isZero();
        assertThat(svc.groupDiscount(3)).isEqualTo(10);
        assertThat(svc.groupDiscount(7)).isEqualTo(20);
        assertThat(svc.groupDiscount(12)).isEqualTo(30);
    }

    @Test
    void frequentDiscount() {
        assertThat(svc.frequentDiscount(0)).isZero();
        assertThat(svc.frequentDiscount(2)).isEqualTo(10);
        assertThat(svc.frequentDiscount(5)).isEqualTo(20);
        assertThat(svc.frequentDiscount(7)).isEqualTo(30);
    }

    @Test
    void birthdayDiscount() {
        // sin cumpleaños
        assertThat(svc.birthdayDiscount(false, 4, 1)).isZero();
        // 1 cumpleañero en grupo de 4 => 50/4 = 12.5
        assertThat(svc.birthdayDiscount(true, 4, 1)).isEqualTo(12.5);
        // 2 cumpleañeros en grupo de 5 => 50/5*2 = 20
        assertThat(svc.birthdayDiscount(true, 5, 2)).isEqualTo(20.0);
    }
}
