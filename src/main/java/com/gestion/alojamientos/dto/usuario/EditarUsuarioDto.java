package com.gestion.alojamientos.dto.usuario;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
/**
*DTO para editar la informacion del usuario existente
* Este objeto se utiliza para actualizar los datos de un usuario
* El ID no debe ser modoficado, ya que es único para cada usuario
*
 */
public record EditarUsuarioDto (
    //ID no debe ser modificado
    @NotNull Long id,
    // Nombre que puede ser modificado
    @Length(max = 50) String nombre,
    // Apellido que puede ser modificado
    @Length(max = 60) String apellido,
    @NotBlank @Pattern(regexp = "\\+57\\d{10}", message = "El telefono debe contener solo números y exactamente 10 digitos") String telefono
    ){
}
