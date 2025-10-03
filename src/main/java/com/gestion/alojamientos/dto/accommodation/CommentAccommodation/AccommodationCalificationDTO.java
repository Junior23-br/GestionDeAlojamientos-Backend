package com.gestion.alojamientos.dto.accommodation.CommentAccommodation;
/**
 * Un DTO (Data Transfer Object) es un objeto plano que se usa para intercambiar datos entre capas
 * Tiene la información basica de la calificacion del alojamiento
 *
 */
public record AccommodationCalificationDTO(

        Long id, Integer cleanLiness, Integer comfort, Integer location, Integer accuracyOfListing,
        Long idAccommodation, Integer comunicationHost, Double prom

) {
}

/**
 *
 *Se evita poner entidades con datos sensibles asociados al DTO por cuestiones de seguiridad, ademas se evita
 * poner entidades en el DTO por posibles problemas de serializacion por relaciones bidireccionales
 */