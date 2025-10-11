package com.gestion.alojamientos.mapper.users;

import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.model.users.Guest;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GuestMapper {
    
    // ==========================================
    // ENTITY → DTO (para consultas o responses)
    // ==========================================
    @Mapping(target = "firstName", source = "name")
    @Mapping(target = "lastName", ignore = true) // no está en la entidad
    @Mapping(target = "birthDate", source = "birthDate") // LocalDate → LocalDate (sin conversión)
    @Mapping(target = "paymentMethodsIds", expression = "java(mapPaymentMethodIds(guest))")
    @Mapping(target = "bookingIds", expression = "java(mapBookingIds(guest))")
    @Mapping(target = "transactionIds", expression = "java(mapTransactionIds(guest))")
    GuestDto toDto(Guest guest);

    // ==========================================
    // CREATE DTO → ENTITY
    // ==========================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", expression = "java(dto.firstName() + '.' + dto.lastName())")
    @Mapping(target = "name", source = "firstName")
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
    @Mapping(target = "name", source = "firstName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "urlProfilePhoto", source = "urlProfilePhoto")
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