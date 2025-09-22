package com.gestion.alojamientos.dto.usuario;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
*DTO para eliminar logicamente a un usuario del sistema
* Este objeto es utilizado para validar la identidad del solicitante
 */
public record EliminarUsuarioDto (
        /**
         * Identificador único del usuario.
         * Campo obligatorio.
         */
        @NotNull Long id,
        /**
         * Contraseña del usuario.
         * Campo obligatorio para validar la eliminación.
         */
        @NotBlank String contrasenia
){
}
