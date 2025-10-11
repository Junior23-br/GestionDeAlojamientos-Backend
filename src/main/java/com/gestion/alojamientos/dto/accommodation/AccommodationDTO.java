package com.gestion.alojamientos.dto.accommodation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Un DTO (Data Transfer Object) es un objeto plano que se usa para intercambiar datos entre capas
 * Tiene la información basica del alojamiento
 *
 */
public record AccommodationDTO(        Long id,
                                       String title,
                                       String accomodationType,
                                       String houseRules,
                                       Long ubicationID,
                                       Integer maxGuestCapacity,
                                       Integer numberOfBeds,
                                       Integer numberOfBathrooms,
                                       String approvalStatus,
                                       String operationalStatus, //
                                       LocalDateTime createdTime,
                                       LocalDateTime updateTime,
                                       Long hostID,
                                       List<Long> bookingsID,
                                       List<Long> accommodationCalificationsIDs,
                                       List<Long> commentaryIDs, //
                                       List<String> urlPhotos,   //
                                       List<ServiceDTO> services ) {

}


/**
 *
 *Se evita poner entidades con datos sensibles asociados al DTO por cuestiones de seguiridad, ademas se evita
 * poner entidades en el DTO por posibles problemas de serializacion por relaciones bidireccionales
 */
