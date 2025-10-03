package com.gestion.alojamientos.dto.accommodation;



import com.gestion.alojamientos.model.accomodation.Services;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Un DTO (Data Transfer Object) es un objeto plano que se usa para intercambiar datos entre capas
 * Tiene la información basica del alojamiento, no la contraseña por temas de seguridad
 *
 */
public record AccommodationDTO(Long id, String title, String accomodationType,
                               String houseRules, Long ubicationID, Integer maxGuestCapacity,
                               Integer numberOfBeds, String approvalStatus, String status,
                               LocalDateTime createdTime, LocalDateTime updatedTime, Long hostID, List<Long> bookingsID,
                               List<Long> accommodationCalificationsIDs, List<Services> services) {

}


/**
 *
 *Se evita poner entidades con datos sensibles asociados al DTO por cuestiones de seguiridad, ademas se evita
 * poner entidades en el DTO por posibles problemas de serializacion por relaciones bidireccionales
 */
