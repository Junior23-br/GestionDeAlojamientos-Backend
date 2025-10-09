package com.gestion.alojamientos.dto.accommodation.CommentHost;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentHostUpdateDTO(
        /**
         * ID del comentario que se desea editar.
         */
        @NotNull(message = "El ID del comentario es obligatorio")
        Long id,

        /**
         * Nuevo contenido del comentario.
         */
        @NotBlank(message = "El contenido no puede estar vacío")
        @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
        String content
) {
}
