package com.gestion.alojamientos.mapper.users;

import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.transaction.FinancialAccount;
import com.gestion.alojamientos.model.transaction.Transaction;
import com.gestion.alojamientos.model.users.Guest;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    // ===============================
    // Entity → DTO
    // ===============================
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "name") // NormalUser.name → firstName
    @Mapping(target = "lastName", ignore = true) // No existe en entidad base
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", expression = "java(convertDateToLocalDate(guest.getBirthDate()))")
    @Mapping(target = "urlProfilePhoto", source = "urlProfilePhoto")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "paymentMethodsIds", expression = "java(mapFinancialAccountsToIds(guest.getPaymentMethods()))")
    @Mapping(target = "bookingIds", expression = "java(mapBookingsToIds(guest.getBookingList()))")
    @Mapping(target = "transactionIds", expression = "java(mapTransactionsToIds(guest.getTransactionHistory()))")
    GuestDto toDto(Guest guest);

    // ===============================
    // DTO → Entity
    // ===============================
    @InheritInverseConfiguration
    @Mapping(target = "paymentMethods", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    @Mapping(target = "transactionHistory", ignore = true)
    @Mapping(target = "password", ignore = true) // No se expone en DTO
    @Mapping(target = "birthDate", expression = "java(convertLocalDateToDate(dto.birthDate()))")
    @Mapping(target = "name", source = "firstName")
    @Mapping(target = "id", source = "id")
    Guest toEntity(GuestDto dto);

    // ===============================
    // Métodos auxiliares
    // ===============================
    default List<Long> mapFinancialAccountsToIds(List<FinancialAccount> accounts) {
        if (accounts == null) return null;
        return accounts.stream()
                .map(FinancialAccount::getId)
                .collect(Collectors.toList());
    }

    default List<Long> mapBookingsToIds(List<Booking> bookings) {
        if (bookings == null) return null;
        return bookings.stream()
                .map(Booking::getId)
                .collect(Collectors.toList());
    }

    default List<Long> mapTransactionsToIds(List<Transaction> transactions) {
        if (transactions == null) return null;
        return transactions.stream()
                .map(Transaction::getId)
                .collect(Collectors.toList());
    }

    // ===============================
    // Conversión de fechas
    // ===============================
    default LocalDate convertDateToLocalDate(Date date) {
        return date != null
                ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
    }

    default Date convertLocalDateToDate(LocalDate localDate) {
        return localDate != null
                ? Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;
    }

}
