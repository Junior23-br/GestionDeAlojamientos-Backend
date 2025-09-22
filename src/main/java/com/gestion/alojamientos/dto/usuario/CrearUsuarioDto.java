package com.gestion.alojamientos.dto.usuario;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;
/**
*DTO para registrar un nuevo cliente
* Validaciones incluidas para garantizar la integridad de los datos
 */
public record CrearUsuarioDto(
        /**
         * Nombre del usuario
         * Campo obligatorio con longitud máxima de 50 caracteres.
         */
        @NotBlank @Length(max = 50) String nombre,
        /**
         * Apellido del usuario
         * Campo obligatorio con longitud máxima de 60 caracteres.
         */
        @NotBlank @Length(max = 60) String apellido,
        /**
         * Telefono del usuario
         * Campo obligatorio con longitud de 10 caracteres y formato de +57
         */
        @NotBlank @Pattern(regexp = "\\+57\\d{10}", message = "El telefono debe contener solo números y exactamente 10 digitos") String telefono,
        /**
         * Fecha de nacimiento del usuario
         * Campo obligatorio con fecha anterior al día presente.
         */
        @NotNull @Past LocalDate fecha_nacimiento,
        /**
         * Correo electronico del usuario
         * Campo obligatorio con formato valido
         */
        @NotBlank @Email String correo_electronico,
        /**
         * Contraseña del usuario
         * Campo obligatorio con longitud mínima de 8 caracteres, cumpliendo las especificaciones
         */
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",message = "La contraseña debe contener mínimo 8 caracteres, entre: mayusculas,minusculas y números")
        String contrasenia

) {
}
