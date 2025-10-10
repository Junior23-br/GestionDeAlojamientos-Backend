package com.gestion.alojamientos.dto.Chat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO utilizado para la creación de un Chat.
 * Contiene las listas de IDs de los mensajes y de los miembros asociados.
 */
public record ChatCreateDTO(

        /**
         * Lista de identificadores de los usuarios (Guests) que participan en el chat.
         * No puede estar vacía, ya que todo chat debe tener al menos un miembro.
         */
        @NotNull(message = "La lista de miembros no puede ser nula.")
        @NotEmpty(message = "El chat debe contener al menos un miembro.")
        List<Long> memberIds

) {}
