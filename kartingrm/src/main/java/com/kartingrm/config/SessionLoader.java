package com.kartingrm.config;

import com.kartingrm.service.SessionService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class SessionLoader implements ApplicationRunner {

    private final SessionService sessionService;

    public SessionLoader(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // obtenemos el lunes de esta semana
        LocalDate monday = LocalDate.now()
                .with(DayOfWeek.MONDAY);
        // Definir aquí tus franjas horarias “de negocio”
        LocalTime[][] slots = {
                { LocalTime.of(10, 0), LocalTime.of(11, 0) },
                { LocalTime.of(11, 0), LocalTime.of(12, 0) },
                { LocalTime.of(15, 0), LocalTime.of(16, 0) }
                // … añade las que necesites
        };
        int defaultCapacity = 15;

        // Para cada día de lunes a domingo y para cada slot, lo creamos si falta
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            for (var slot : slots) {
                sessionService.createIfAbsent(
                        date,
                        slot[0],
                        slot[1],
                        defaultCapacity
                );
            }
        }
    }
}
