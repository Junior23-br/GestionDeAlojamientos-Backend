package com.gestion.alojamientos.dto.accommodation.CommentHost;

import jakarta.validation.constraints.NotNull;

public record DeleteCommentHostDTO(
        /**
         * ID del comentario a eliminar.
         */
        @NotNull(message = "El ID del comentario es obligatorio")
        Long id,

        /**
         * ID del usuario (Guest) que realizó el comentario.
         * Se usa para verificar que el autor es quien solicita la eliminación.
         */
        @NotNull(message = "El ID del autor es obligatorio")
        Long senderId
) {
}
