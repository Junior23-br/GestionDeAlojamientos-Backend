package com.gestion.alojamientos.dto.guest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
/**
*DTO para editar la informacion del huesped existente
* Este objeto se utiliza para actualizar los datos de un huesped
* El ID no debe ser modoficado, ya que es único para cada huesped
 */
public record EditGuestDto(
    //ID no debe ser modificado
    @NotNull Long id,
    // Nombre que puede ser modificado
    @Length(max = 50) String firstName,
    // Apellido que puede ser modificado
    @Length(max = 60) String lastName,
    @NotBlank @Pattern(regexp = "\\+57\\d{10}", message =
            "El phoneNumber debe contener solo números y exactamente 10 digitos") String phoneNumber,
          String urlProfilePhoto
){
}
