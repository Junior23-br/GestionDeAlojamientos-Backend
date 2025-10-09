package com.gestion.alojamientos.dto.accommodation.CommentHost;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentHostCreateDTO(
        /**
         * ID del usuario (Guest) que envía el comentario.
         */
        @NotNull(message = "El ID del remitente no puede ser nulo")
        Long senderId,

        /**
         * ID del Host que recibe el comentario.
         */
        @NotNull(message = "El ID del receptor no puede ser nulo")
        Long receiverId,

        /**
         * Contenido del comentario.
         */
        @NotBlank(message = "El comentario no puede estar vacío")
        @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
        String content
) {
}
