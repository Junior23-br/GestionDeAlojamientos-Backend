package com.gestion.alojamientos.dto.guest;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import java.time.LocalDate;
import java.util.List;
/**
 * Un DTO (Data Transfer Object) es un objeto plano que se usa para intercambiar datos entre capas.
 *
 * Este DTO contiene la información básica de un huésped (Guest) del sistema,
 * combinando los datos heredados de las clases SuperUser y NormalUser, además de los propios de Guest.
 *
 * Se evita incluir entidades completas con relaciones (como Booking o FinancialAccount)
 * por cuestiones de seguridad y para prevenir problemas de serialización con relaciones bidireccionales.
 */
public record GuestDto(

        /**
         * Identificador único del huésped.
         */
        Long id,

        /**
         * Correo electrónico del huésped.
         */
        String email,

        /**
         * Nombre de usuario
         */
        String username,

        /**
         * Nombre del huésped.
         */
        String firstName,

        /**
         * Apellido del huésped.
         */
        String lastName,


        /**
         * Número de teléfono del huésped.
         */
        String phoneNumber,

        /**
         * Fecha de nacimiento del huésped.
         */
        LocalDate birthDate,

        /**
         * URL de la foto de perfil del huésped.
         */
        String urlProfilePhoto,

        /**
         * Estado actual del huésped: ACTIVE, DELETED, SUSPENDED, INACTIVE.
         */
        StatesOfGuest state,

        /**
         * Lista de IDs de cuentas financieras asociadas (métodos de pago).
         */
        List<Long> paymentMethodsIds,

        /**
         * Lista de IDs de reservas realizadas por el huésped.
         */
        List<Long> bookingIds,

        /**
         * Lista de IDs de transacciones realizadas por el huésped.
         */
        List<Long> transactionIds

) { }
