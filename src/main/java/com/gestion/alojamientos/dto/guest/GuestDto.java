package com.gestion.alojamientos.dto.guest;
/**
*DTO representa los datos completos de un huesped en el sistema
* Incluye la info basica heredada y su informacion especifica
*
 */
public record GuestDto (
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        java.time.LocalDate birthDate,
        String email
) {
}
