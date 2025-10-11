package com.gestion.alojamientos.dto.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record ResetPasswordDto(
        @NotBlank(message = "El email es requerido")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "El código de recuperación es requerido")
        String resetCode,
        @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message="La contraseña debe contener mínimo 8 caracteres, entre: mayusculas,minusculas y números")
        String newPassword
)
{

}
