package com.kartingrm.dto;

public record ReservationResponseDTO(
        Long id,
        String reservationCode,
        ClientDTO client,
        SessionDTO session,
        Integer participants,
        RateType rateType,
        Double basePrice,
        Double discountPercentage,
        Double finalPrice,
        ReservationStatus status
) {}
