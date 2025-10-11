package com.gestion.alojamientos.service;

import java.util.List;

import com.gestion.alojamientos.dto.accommodation.*;

public interface AccomodationService {

    AccommodationDTO createAccommodation(Long hostId, AccommodationCreateDTO accommodationDTO) throws Exception;
    AccommodationDTO getAccommodationById(Long accommodationId) throws Exception;
    AccommodationDTO updateAccommodation(Long accommodationId, AccommodationUpdateDTO accommodationDTO) throws Exception;
    boolean softDeleteAccommodation(Long accommodationId, Long hostId) throws  Exception;
    List<AccommodationDTO> getHostAccommodations(Long hostId);
}
