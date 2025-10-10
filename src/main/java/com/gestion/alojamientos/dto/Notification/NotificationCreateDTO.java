package com.gestion.alojamientos.dto.Notification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

/**
 * DTO utilizado para la creación de una nueva notificación dentro del sistema.
 * Contiene los datos necesarios para registrar una notificación enviada a un usuario.
 */
public record NotificationCreateDTO(

        /**
         * Indica si la notificación ha sido leída.
         * Generalmente se inicializa como false al momento de la creación.
         */
        @NotNull(message = "El estado de lectura no puede ser nulo.")
        Boolean read,

        /**
         * Título de la notificación.
         */
        @NotNull(message = "El título no puede ser nulo.")
        @NotEmpty(message = "El título no puede estar vacío.")
        String title,

        /**
         * Identificador del mensaje asociado a la notificación.
         * Puede ser nulo si la notificación no está vinculada a un mensaje específico.
         */
        Long messageId,

        /**
         * Clasificación de la notificación (por ejemplo, ALERTA, INFORMACIÓN, ADVERTENCIA).
         */
        @NotNull(message = "La clasificación de la notificación no puede ser nula.")
        String clasificationNotification,

        /**
         * Identificador del usuario receptor de la notificación.
         */
        @NotNull(message = "El ID del receptor no puede ser nulo.")
        String receiverId

) {}
