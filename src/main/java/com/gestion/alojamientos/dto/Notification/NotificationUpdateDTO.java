package com.gestion.alojamientos.dto.Notification;

import jakarta.validation.constraints.NotNull;


/**
 * DTO utilizado para la actualización de una notificación existente.
 * Permite modificar su estado de lectura, título, mensaje asociado o clasificación.
 */
public record NotificationUpdateDTO(

        /**
         * Identificador único de la notificación que se desea actualizar.
         */
        @NotNull(message = "El ID de la notificación no puede ser nulo.")
        Long id,

        /**
         * Indica si la notificación ha sido leída.
         */
        Boolean read


) {}
