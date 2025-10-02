package com.gestion.alojamientos.dto.admin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para registrar un nuevo administrador.
 * Incluye validaciones para garantizar la integridad de los datos.
 */
public record CreateAdminDto(
        /**
         * id administrador.
         * Campo obligatorio con formato válido.
         */
        @NotBlank
        @Email
        String id,


        /**
         * Correo electrónico del administrador.
         * Campo obligatorio con formato válido.
         */
        @NotBlank
        @Email
        String email,

        /**
         * Contraseña del administrador.
         * Campo obligatorio con longitud mínima de 8 caracteres,
         * incluyendo al menos una mayúscula, una minúscula y un número.
         */
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "La contraseña debe contener mínimo 8 caracteres, incluyendo: mayúsculas, minúsculas y números"
        )
        String password
) {
}
