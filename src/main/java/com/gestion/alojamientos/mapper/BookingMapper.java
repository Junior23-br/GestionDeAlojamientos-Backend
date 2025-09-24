package com.gestion.alojamientos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gestion.alojamientos.dto.BookingDTO;
import com.gestion.alojamientos.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {



    @Mapping(target = "id", source = "id")
    @Mapping(target = "guestId", source = "guestId")
    @Mapping(target = "accommodationId", source = "accommodationId")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "totalPrice", source = "totalPrice")
    Booking toEntity(BookingDTO dto);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "guestId", target = "guestId")
    @Mapping(source = "accommodationId", target = "accommodationId")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalPrice", target = "totalPrice")
    Booking toDto(BookingDTO dto);
    
}
