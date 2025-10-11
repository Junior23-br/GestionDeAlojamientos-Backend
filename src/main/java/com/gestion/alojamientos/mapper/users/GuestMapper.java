package com.gestion.alojamientos.mapper.users;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.model.users.Guest;

@Mapper(componentModel = "spring")
public interface GuestMapper {
    
    // ==========================================
    // ENTITY → DTO (para consultas o responses)
    // ==========================================
    @Mapping(target = "name", source = "name")
    @Mapping(target = "birthDate", source = "birthDate") // LocalDate → LocalDate (sin conversión)
    @Mapping(target = "paymentMethodsIds", expression = "java(mapPaymentMethodIds(guest))")
    @Mapping(target = "bookingIds", expression = "java(mapBookingIds(guest))")
    @Mapping(target = "transactionIds", expression = "java(mapTransactionIds(guest))")
    @Mapping(target = "urlProfilePhoto", ignore = true)
    GuestDto toDto(Guest guest);

    // ==========================================
    // CREATE DTO → ENTITY
    // ==========================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "name") //  name directamente para username
    @Mapping(target = "name", source = "name")
    @Mapping(target = "state", expression = "java(StatesOfGuest.ACTIVE)")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "birthDate", source = "birthDate") // LocalDate directo
    @Mapping(target = "paymentMethods", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    @Mapping(target = "transactionHistory", ignore = true)
    @Mapping(target = "resetCode", ignore = true)
    @Mapping(target = "urlProfilePhoto", ignore = true)
    Guest toEntity(CreateGuestDto dto);

    // ==========================================
    // EDIT DTO → ENTITY (actualización parcial)
    // ==========================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "paymentMethods", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    @Mapping(target = "transactionHistory", ignore = true)
    @Mapping(target = "resetCode", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "urlProfilePhoto", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateFromDto(EditGuestDto dto, @MappingTarget Guest guest);

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    default List<Long> mapPaymentMethodIds(Guest guest) {
        if (guest.getPaymentMethods() == null) return List.of();
        return guest.getPaymentMethods()
                .stream()
                .map(pm -> pm.getId())
                .collect(Collectors.toList());
    }

    default List<Long> mapBookingIds(Guest guest) {
        if (guest.getBookingList() == null) return List.of();
        return guest.getBookingList()
                .stream()
                .map(b -> b.getId())
                .collect(Collectors.toList());
    }

    default List<Long> mapTransactionIds(Guest guest) {
        if (guest.getTransactionHistory() == null) return List.of();
        return guest.getTransactionHistory()
                .stream()
                .map(t -> t.getId())
                .collect(Collectors.toList());
    }
}