package com.gestion.alojamientos.dto.Chat;

import jakarta.validation.constraints.NotNull;

/**
 * DTO utilizado para la eliminación de un Chat.
 * Contiene únicamente el identificador del chat que se desea eliminar.
 */
public record ChatDeleteDTO(

        /**
         * Identificador único del chat a eliminar.
         * No puede ser nulo, ya que es necesario para localizar el registro en la base de datos.
         */
        @NotNull(message = "El ID del chat no puede ser nulo.")
        Long id

) {}
