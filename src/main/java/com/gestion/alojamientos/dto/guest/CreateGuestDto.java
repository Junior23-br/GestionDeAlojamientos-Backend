package com.gestion.alojamientos.dto.guest;
import com.gestion.alojamientos.model.enums.Role;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;
/**
*DTO para registrar un nuevo huesped
* Validaciones incluidas para garantizar la integridad de los datos
 */
public record CreateGuestDto(
        /**
         * Nombre del usuario
         * Campo obligatorio con longitud máxima de 50 caracteres.
         */
        @NotBlank @Length(max = 50) String firstName,
        /**
         * Apellido del usuario
         * Campo obligatorio con longitud máxima de 60 caracteres.
         */
        @NotBlank @Length(max = 60) String lastName,
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
         * Contraseña del usuario
         * Campo obligatorio con longitud mínima de 8 caracteres, cumpliendo las especificaciones
         */
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",message = "La contraseña debe contener mínimo 8 caracteres, entre: mayusculas,minusculas y números")
        String password,
        /**
         * Rol del usuario
         * Campo obligatorio, debe ser GUEST o HOST
         */
        @NotNull(message = "El rol es obligatorio") Role role
) {
}
