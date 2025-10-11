package com.gestion.alojamientos.dto.Host;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import com.gestion.alojamientos.model.enums.Role;

import java.time.LocalDate;

/**
 * DTO para registrar un nuevo HOST en el sistema
 * Validaciones incluidas para cargar toda la informacion
 */
public record HostCreateDTO(

        /**
         * Nombre del usuario
         * Campo obligatorio con longitud máxima de 100 caracteres.
         */
        @NotBlank @Length(max = 100) String name,

        /**
         * Telefono del usuario
         * Campo obligatorio con longitud de 10 caracteres y formato de +57
         */
        @NotBlank @Pattern(regexp = "\\+57\\d{10}", message = "El phoneNumber debe contener solo números y exactamente 10 digitos")
        String phoneNumber,
        /**
         * Fecha de nacimiento del usuario
         * Campo obligatorio con fecha anterior al día presente.
         */
        @NotNull @Past LocalDate birthDate,
        /**
         * Correo electronico del usuario
         * Campo obligatorio con formato valido
         */
        @NotBlank @Email String email,

        /**
         * Descripcion del host
         * Campo obligatorio
         */
        @NotBlank
        String personalDescription,

        /**
         * Contraseña del usuario
         * Campo obligatorio con longitud mínima de 8 caracteres, cumpliendo las especificaciones
         */
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",message = "La contraseña debe contener mínimo 8 caracteres, entre: mayusculas,minusculas y números")
        String password,

        Role role

) {
}
