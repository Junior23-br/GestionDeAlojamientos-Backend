package com.gestion.alojamientos.dto.admin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
/**
 *DTO para editar la contraseña del admin existente
 * Este objeto se utiliza para actualizar la contraseña del admin
 * El ID no debe ser modoficado, ya que es único para cada Admin
 * El correo no debe ser modificado ya que es con el que se reconoce el Admin
 */
public record EditAdminDto(
        //ID no debe ser modificado
        @NotNull Long id,
        //ID no debe ser modificado
        @NotNull String email,

        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",message = "La contraseña debe contener mínimo 8 caracteres, entre: mayusculas,minusculas y números")
        String password
) {
}
