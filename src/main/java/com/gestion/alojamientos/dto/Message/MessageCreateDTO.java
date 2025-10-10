package com.gestion.alojamientos.dto.Message;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

/**
 * DTO utilizado para la creación de un nuevo mensaje dentro de un chat.
 * Contiene la información necesaria para registrar un mensaje enviado entre dos usuarios.
 */
public record MessageCreateDTO(

        /**
         * Identificador del usuario que envía el mensaje.
         */
        @NotNull(message = "El ID del remitente no puede ser nulo.")
        Long senderId,

        /**
         * Identificador del usuario que recibe el mensaje.
         */
        @NotNull(message = "El ID del receptor no puede ser nulo.")
        Long receiverId,

        /**
         * Texto del mensaje.
         */
        @NotNull(message = "El texto del mensaje no puede ser nulo.")
        @NotEmpty(message = "El texto del mensaje no puede estar vacío.")
        String text,

        /**
         * Documento adjunto en formato binario (puede ser nulo si el mensaje no contiene archivo).
         */
        byte[] doc,

        /**
         * Identificador del chat al que pertenece el mensaje.
         */
        @NotNull(message = "El ID del chat no puede ser nulo.")
        Long chatId

) {}
