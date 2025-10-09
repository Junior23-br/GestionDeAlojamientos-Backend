package com.gestion.alojamientos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
        /**
         * Correo electrónico del usuario.
         * Debe ser un email válido.
         */
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        /**
         * Contraseña del usuario.
         * No puede estar vacía.
         */
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}