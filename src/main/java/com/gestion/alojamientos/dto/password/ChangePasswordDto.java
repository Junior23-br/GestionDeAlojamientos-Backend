package com.gestion.alojamientos.dto.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordDto(
        @NotBlank(message = "La contraseña actual es requerida")
        String currentPassword,
        @NotBlank(message = "La nueva contraseña es requerida")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",message =
                "La contraseña debe contener mínimo 8 caracteres, entre: mayusculas,minusculas y números")
        String newPassword
)
{
}
