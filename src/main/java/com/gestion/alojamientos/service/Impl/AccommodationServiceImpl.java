package com.gestion.alojamientos.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.dto.accommodation.ServiceDTO;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
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
import lombok.extern.slf4j.Slf4j;

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



    @Override
    public AccommodationDTO createAccommodation(Long hostId, AccommodationDTO accommodationDTO) throws Exception {
        // Validar que el host existe
        Host host = hostRepo.findById(hostId)
            .orElseThrow(() -> new EntityNotFoundException("Host no encontrado con ID: " + hostId));

        // Validar título único por host
        if (accommodationRepo.existsByHostIdAndTitle(hostId, accommodationDTO.title())) {
            throw new Exception("Ya existe un alojamiento con este título para este anfitrión");
        }

        // Mapear DTO a entidad
        Accomodation accommodation = accommodationMapper.toEntity(accommodationDTO);
        
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
        if (accommodationDTO.services() != null) {
            List<Services> validServices = validateAndGetServices(accommodationDTO.services());
            accommodation.setServicesList(validServices);
        }

        // Guardar alojamiento
        Accomodation savedAccommodation = accommodationRepo.save(accommodation);
        
        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    public AccommodationDTO getAccommodationById(Long accommodationId) {
        Accomodation accommodation = accommodationRepo.findByIdWithCompleteDetails(accommodationId)
            .orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado con ID: " + accommodationId));
        
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public AccommodationDTO updateAccommodation(Long accommodationId, AccommodationDTO accommodationDTO) throws Exception {
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
        if (accommodationDTO.services() != null) {
            List<Services> updatedServices = validateAndGetServices(accommodationDTO.services());
            existingAccommodation.setServicesList(updatedServices);
        }

        Accomodation updatedAccommodation = accommodationRepo.save(existingAccommodation);
        return accommodationMapper.toDto(updatedAccommodation);
    }

    @Override
    public boolean softDeleteAccommodation(Long accommodationId, Long hostId) throws Exception {
        Accomodation accommodation = accommodationRepo.findById(accommodationId)
            .orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado"));

        // Validar propiedad
        if (!accommodation.getHost().getId().equals(hostId)) {
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


    @Override
    public AccommodationDTO getAccommodationDetails(Long accommodationId) throws Exception {
        Accomodation accommodation = accommodationRepo.findByIdWithCompleteDetails(accommodationId)
            .orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado con ID: " + accommodationId));

        // Solo mostrar alojamientos aprobados y operacionales
        if (accommodation.getApprovalStatus() != ApprovalStatus.APPROVED ||
            accommodation.getOperationalStatus() != OperationalStatus.ACTIVE) {
            throw new Exception("El alojamiento no está disponible");
        }

        return accommodationMapper.toDto(accommodation);
    }

    // Método auxiliar para validar y obtener servicios
    private List<Services> validateAndGetServices(List<ServiceDTO> serviceDTOs) {
        return serviceDTOs.stream()
            .map(serviceDTO -> servicesRepo.findById(serviceDTO.id())
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado: " + serviceDTO.id())))
            .collect(Collectors.toList());
    }
}