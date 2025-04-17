package com.kartingrm.mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationResponseDTO toDto(Reservation entity);
}