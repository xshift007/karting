package com.kartingrm.dto;

import com.kartingrm.entity.RateType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequestDTO(
        @NotBlank(message = "El código de reserva no puede estar vacío")
        String reservationCode,

        @NotNull(message = "El ID del cliente es obligatorio")
        Long clientId,

        @NotNull(message = "La fecha de la sesión es obligatoria")
        @FutureOrPresent(message = "La fecha de sesión debe ser hoy o futura")
        LocalDate sessionDate,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime startTime,

        @NotNull(message = "La hora de término es obligatoria")
        LocalTime endTime,

        @NotNull(message = "La cantidad de participantes es obligatoria")
        @Min(value = 1, message = "Debe haber al menos 1 participante")
        @Max(value = 15, message = "Máximo 15 participantes")
        Integer participants,

        @NotNull(message = "El tipo de tarifa es obligatorio")
        RateType rateType
) {
    @AssertTrue(message = "La hora de término debe ser posterior a la de inicio")
    public boolean isTimeOrder() {
        return endTime.isAfter(startTime);
    }
}
