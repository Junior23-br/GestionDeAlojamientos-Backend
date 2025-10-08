package com.gestion.alojamientos.dto.Host;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record  DeleteHostDTO (
        @NotNull@NotBlank@NotEmpty
        Long id, //Identificador del host
        @NotNull@NotBlank@NotEmpty
        String password //para validar


) {
}
