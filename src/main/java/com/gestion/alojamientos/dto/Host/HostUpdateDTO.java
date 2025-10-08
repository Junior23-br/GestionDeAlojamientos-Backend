package com.gestion.alojamientos.dto.Host;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;


/**
 *DTO para editar la informacion del Host existente
 * Este objeto se utiliza para actualizar los datos de un Host
 * El ID no debe ser modoficado, ya que es único para cada Host
 */
public record HostUpdateDTO(

        //ID no debe ser modificado
        @NotNull Long id,
        // Nombre que puede ser modificado
        @Length(max = 50) String name,
        @NotBlank @Pattern(regexp = "\\+57\\d{10}", message =
                "El phoneNumber debe contener solo números y exactamente 10 digitos") String phoneNumber,
        @NotBlank String personalDescription,
        String urlProfilePhoto
) {
}
