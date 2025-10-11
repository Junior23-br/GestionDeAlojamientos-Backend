package com.gestion.alojamientos.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.alojamientos.dto.accommodation.AccommodationCreateDTO;
import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.dto.accommodation.AccommodationUpdateDTO;
import com.gestion.alojamientos.dto.accommodation.ServiceDTO;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.mapper.accomodation.ServicesMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.accomodation.Services;
import com.gestion.alojamientos.model.accomodation.Ubication;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.accomodation.ServicesRepo;
import com.gestion.alojamientos.repository.accomodation.UbicationRepo;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.AccomodationService;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AccommodationServiceImpl implements AccomodationService {

    @Autowired
    private AccommodationRepo accommodationRepo;
    @Autowired
    private  HostRepo hostRepo;
    @Autowired
    private UbicationRepo ubicationRepo;
    @Autowired
    private ServicesRepo servicesRepo;
    @Autowired
    private AccommodationMapper accommodationMapper;
    @Autowired
    private ServicesMapper servicesMapper;

    @Override
    public AccommodationDTO createAccommodation(Long hostId, AccommodationCreateDTO accommodationDTO) throws Exception {
        // Validar que el host existe
        Host host = hostRepo.findById(hostId)
            .orElseThrow(() -> new EntityNotFoundException("Host no encontrado con ID: " + hostId));

        // Validar título único por host
        if (accommodationRepo.existsByHostIdAndTitle(hostId, accommodationDTO.title())) {
            throw new Exception("Ya existe un alojamiento con este título para este anfitrión");
        }

        // Mapear DTO a entidad
        Accomodation accommodation = accommodationMapper.toEntity(accommodationDTO);
        System.out.println("Accommodation mapped: " + accommodation.getAccomodationType());
        // Establecer relaciones y datos del sistema
        accommodation.setHost(host);
        accommodation.setApprovalStatus(ApprovalStatus.PENDING);
        accommodation.setOperationalStatus(OperationalStatus.ACTIVE);
        accommodation.setCreatedTime(LocalDateTime.now());
        accommodation.setUpdateTime(LocalDateTime.now());

        // Guardar ubicación si es nueva
        if (accommodation.getUbication() != null) {
            Ubication savedUbication = ubicationRepo.save(accommodation.getUbication());
            accommodation.setUbication(savedUbication);
        }

        // Validar y asociar servicios
        if (accommodationDTO.serviceIds() != null) {
            List<Services> validServices = validateAndGetServices(accommodationDTO.serviceIds());
            accommodation.setServicesList(validServices);
        }

        // Guardar alojamiento
        Accomodation savedAccommodation = accommodationRepo.save(accommodation);
        
        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    public AccommodationDTO getAccommodationById(Long accommodationId) {
        Accomodation accommodation = accommodationRepo.findBaseById(accommodationId).orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado con ID: " + accommodationId));
        // Paso 2: carga las colecciones usando las mismas entidades persistentes
        accommodationRepo.findByIdWithPhotos(accommodationId).ifPresent(a -> accommodation.setUrlPhotos(a.getUrlPhotos()));
        accommodationRepo.findByIdWithPhotos(accommodationId).ifPresent(a -> accommodation.setServicesList(a.getServicesList()));
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public AccommodationDTO updateAccommodation(Long accommodationId, AccommodationUpdateDTO accommodationDTO) throws Exception {
        Accomodation existingAccommodation = accommodationRepo.findById(accommodationId)
            .orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado con ID: " + accommodationId));

        // Validar título único si cambió
        if (!existingAccommodation.getTitle().equals(accommodationDTO.title()) &&
            accommodationRepo.existsByHostIdAndTitle(existingAccommodation.getHost().getId(), accommodationDTO.title())) {
            throw new Exception("Ya existe un alojamiento con este título");
        }

        // Actualizar campos permitidos
        accommodationMapper.updateEntityFromDTO(accommodationDTO, existingAccommodation);
        existingAccommodation.setUpdateTime(LocalDateTime.now());

        // Actualizar servicios si se proporcionan
        if (accommodationDTO.serviceIds() != null) {
            List<Services> updatedServices = validateAndGetServices(accommodationDTO.serviceIds());
            existingAccommodation.setServicesList(updatedServices);
        }

        Accomodation updatedAccommodation = accommodationRepo.save(existingAccommodation);
        return accommodationMapper.toDto(updatedAccommodation);
    }

    @Override
    public boolean softDeleteAccommodation(Long accommodationId, Long hostId) throws Exception {
        Accomodation accommodation = accommodationRepo.findById(accommodationId)
            .orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado"));
        System.out.println("Host ID: " + accommodation.getHost().getId());
        System.out.println("HOST ID PROPORCIONADO "+ hostId);
        // Validar propiedad
        if (accommodation.getHost().getId() != (hostId)) {
            throw new Exception("No tienes permisos para eliminar este alojamiento");
        }

        // Validar que no tenga reservas futuras (usando el repositorio de reservas)
        // boolean hasFutureBookings = bookingRepo.existsFutureBookings(accommodationId);
        // if (hasFutureBookings) {
        //     throw new BusinessException("No se puede eliminar, tiene reservas futuras");
        // }

        // Soft delete
        accommodation.setOperationalStatus(OperationalStatus.DELETED);
        accommodation.setUpdateTime(LocalDateTime.now());
        accommodationRepo.save(accommodation);

        return true;
    }

    @Override
    public List<AccommodationDTO> getHostAccommodations(Long hostId) {
        // Validar que el host existe
        if (!hostRepo.existsById(hostId)) {
            throw new EntityNotFoundException("Host no encontrado con ID: " + hostId);
        }

        List<Accomodation> accommodations = accommodationRepo.findByHostId(hostId);
        return accommodations.stream()
            .map(accommodationMapper::toDto)
            .collect(Collectors.toList());
    }

    // Método auxiliar para validar y obtener servicios
    private List<Services> validateAndGetServices(List<Long> serviceDTOs) {
        List<Services> services = new ArrayList<>();
        for (Long serviceDTO : serviceDTOs) {
            servicesRepo.findById(serviceDTO)
                .ifPresent(s -> services.add(servicesMapper.toEntity(new ServiceDTO(s.getId(), s.getName()))));
        }
        return services;
    }
}