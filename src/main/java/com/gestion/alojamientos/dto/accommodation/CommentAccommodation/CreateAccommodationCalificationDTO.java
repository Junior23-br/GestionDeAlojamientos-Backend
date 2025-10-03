package com.gestion.alojamientos.dto.accommodation.CommentAccommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateAccommodationCalificationDTO(

        /**
         * Campo para asignar la calificacion referente a la limpieza del alojamiento dejada por el huesped
         * con la reserva finalizada
         *Necesariamente no es obligatoria
         */

        Integer cleanLiness,


        /**
         * Campo para asignar la calificacion referente a la comodidad del alojamiento dejada por el huesped
         * con la reserva finalizada
         *Necesariamente no es obligatoria
         */

        Integer comfort,


        /**
         * Campo para asignar la calificacion referente a la ubicacion del alojamiento dejada por el huesped
         * con la reserva finalizada
         *Necesariamente no es obligatoria
         */

        Integer location,


        /**
         * Campo para asignar la calificacion referente a la presicion del anuncio, osea si
         * el alojamiento si tiene lo que dice el alojamiento, dejada por el huesped
         * con la reserva finalizada
         *Necesariamente no es obligatoria
         */

        Integer accuractOfListing,


        /**
         * Referencia al Id del alojamiento a el que se hizo la calificacion
         * Este campo debe de ser obligatoria, no nulo, no blanco ni vacio
         */
        @NotBlank  @NotNull @NotEmpty
        Long idAccommodation,


        /**
         * Campo para asignar la calificacion referente a la COMUNICACION con el host del alojamiento dejada por el huesped
         * con la reserva finalizada
         *Necesariamente no es obligatoria
         */

        Integer comunicationHost




) {
}
