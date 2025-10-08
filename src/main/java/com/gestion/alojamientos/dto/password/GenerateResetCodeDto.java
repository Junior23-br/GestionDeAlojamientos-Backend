package com.gestion.alojamientos.dto.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GenerateResetCodeDto(
        @NotBlank
        @Email(message = "Email inválido")
        String email
) {
}
