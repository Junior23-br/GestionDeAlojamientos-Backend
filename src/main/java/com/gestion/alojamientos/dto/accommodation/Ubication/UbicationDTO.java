package com.gestion.alojamientos.dto.accommodation.Ubication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UbicationDTO(

        /**
         * Identificacion de la ubicacion en la base de datos
         * Campo obligatorio
         */
        @NotBlank @NotEmpty
        Long id,

        /**
         * Direccion asociada a la ubicacion del alojamiento
         * Campo obligatorio
         * Validaciones
         */
        @NotNull @NotEmpty @NotBlank
        String direction,


        /**
         * Ciudad asociada a la ubicacion del alojamiento (String, se convertirá a Enum en la capa de servicio)
         * Validaciones para que no este en blanco, nulo o vacio
         */
        @NotBlank @NotNull @NotEmpty
        String city,


        /**
         * Numero que hace referencia a la latitud de la ubicacion del alojamiento
         * Validaciones para que no este en blanco, nulo o vacio
         */
        @NotBlank @NotNull @NotEmpty
        Double latitud,


        /**
         * Numero que hace referencia a la longitud de la ubicacion del alojamiento
         * Validaciones para que no este en blanco, nulo o vacio
         */
        @NotBlank @NotNull @NotEmpty
        Double longitud

) {
}
