package com.gestion.alojamientos.dto.accommodation;

import com.gestion.alojamientos.dto.Ubication.UbicationCreateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
/**
 *DTO para editar la informacion del alojamiento existente
 * Este objeto se utiliza para actualizar los datos de un alojamiento
 * El ID no debe ser modificado, ya que es único para cada alojamiento
 */
public record AccommodationUpdateDTO(
        /**
         * ID no debe ser modificado
         */

        @NotNull Long id,
        /**
         * Titulo del alojamiento
         */
        @NotBlank @NotNull @NotEmpty
        String title,

        /**
         * Tipo de alojamiento (String, se convertirá a Enum en la capa de servicio)
         */
        @NotBlank @NotNull @NotEmpty
        String accommodationType,

        /**
         * Reglas de la casa
         */
        String houseRules,

        /**
         * Ubicacion del alojamiento
         * DTO simple, no la entidad completa
         */
        @NotNull
        UbicationCreateDTO ubication,

        /**
         * Capacidad máxima de huéspedes
         */
        @NotNull
        Integer maxGuestCapacity,

        /**
         * Número de camas
         */
        @NotNull
        Integer numberOfBeds,

        /**
         * Número de baños
         */
        @NotNull
        Integer numberOfBathrooms,

        /**
         * ID del host (solo referencia)
         */
        @NotNull
        Long hostId,

        /**
         * URLs de fotos
         */
        List<String> urlPhotos,

        /**
         * Lista de IDs de servicios
         */
        List<Long> serviceIds


) {

}
