package com.gestion.alojamientos.dto.Chat;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO utilizado para la actualización de un Chat existente.
 * Permite modificar los miembros y los mensajes asociados al chat.
 */
public record ChatUpdateDTO(

        /**
         * Identificador único del chat que se desea actualizar.
         */
        @NotNull(message = "El ID del chat no puede ser nulo.")
        Long id,

        /**
         * Nueva lista de identificadores de mensajes asociados al chat.
         * Puede ser nula si no se desea modificar los mensajes actuales.
         */
        List<Long> messageIds,

        /**
         * Nueva lista de identificadores de miembros (Guests) del chat.
         * Puede ser nula si no se desea modificar los miembros actuales.
         */
        List<Long> memberIds

) {}
