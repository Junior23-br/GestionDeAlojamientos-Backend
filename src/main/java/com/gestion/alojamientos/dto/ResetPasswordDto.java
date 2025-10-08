package com.gestion.alojamientos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


public record ResetPasswordDto (
        String email,
        String resetCode,
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",message =
                "La contraseña debe contener mínimo 8 caracteres, entre: mayusculas,minusculas y números")
        String newPassword
) {

}
