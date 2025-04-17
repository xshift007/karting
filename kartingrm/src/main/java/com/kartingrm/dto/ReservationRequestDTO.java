package com.kartingrm.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequestDTO(
        @NotBlank String reservationCode,
        @NotNull Long clientId,
        @NotNull LocalDate sessionDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @Min(1) @Max(15) Integer participants,
        @NotNull RateType rateType
) {}
