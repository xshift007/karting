package com.kartingrm.service.pricing;

import com.kartingrm.entity.RateType;
import com.kartingrm.entity.TariffConfig;
import com.kartingrm.repository.TariffConfigRepository;
import com.kartingrm.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffConfigRepository tariffs;
    private final HolidayService        holidays;

    public TariffConfig forDate(LocalDate date, RateType requested) {

        // 1) El front puede forzar WEEKEND o HOLIDAY
        if (requested == RateType.WEEKEND || requested == RateType.HOLIDAY)
            return tariffs.getReferenceById(requested);

        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY)
            return tariffs.getReferenceById(RateType.WEEKEND);

        if (holidays.isHoliday(date))
            return tariffs.getReferenceById(RateType.HOLIDAY);

        return tariffs.getReferenceById(requested);        // tarifa “normal”
    }
}
