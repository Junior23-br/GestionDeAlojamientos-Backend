package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.dto.accommodation.AccommodationUpdateDTO;
import com.gestion.alojamientos.dto.accommodation.Ubication.UbicationCreateDTO;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.service.Impl.AccommodationServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceUpdateAccommodationTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private AccommodationMapper accommodationMapper;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    private Accomodation existingAccommodation;
    private AccommodationUpdateDTO validUpdateDTO;
    private AccommodationDTO expectedDTO;

    @BeforeEach
    void setUp() {
        // Setup existing accommodation
        Host testHost = new Host();
        testHost.setId(1L);
        testHost.setName("Test Host");

        existingAccommodation = new Accomodation();
        existingAccommodation.setId(1L);
        existingAccommodation.setTitle("Casa de Playa Original");
        existingAccommodation.setHost(testHost);
        existingAccommodation.setApprovalStatus(ApprovalStatus.APPROVED);
        existingAccommodation.setOperationalStatus(OperationalStatus.ACTIVE);
        existingAccommodation.setCreatedTime(LocalDateTime.now().minusDays(30));
        existingAccommodation.setUpdateTime(LocalDateTime.now().minusDays(1));

        // Setup valid update DTO
        validUpdateDTO = new AccommodationUpdateDTO(
                1L,
                "Casa de Playa Actualizada",
                "HOUSE",
                "No fumar, no mascotas, no fiestas",
                new UbicationCreateDTO("Calle 456", "Ciudad Nueva", 41.8781, -87.6298),
                6,
                3,
                2,
                1L,
                Arrays.asList("photo1.jpg", "photo2.jpg", "photo3.jpg"),
                Arrays.asList(1L, 2L, 3L)
        );

        // Setup expected DTO
        expectedDTO = new AccommodationDTO(
                1L,
                "Casa de Playa Actualizada",
                "HOUSE",
                "No fumar, no mascotas, no fiestas",
                1L,
                6,
                3,
                2,
                "APPROVED",
                "ACTIVE",
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                1L,
                null,
                null,
                null,
                Arrays.asList("photo1.jpg", "photo2.jpg", "photo3.jpg"),
                null
        );
    }
    // Actualización exitosa de alojamiento

    @Test
    void updateAccommodation_Success() throws Exception {
        // Arrange
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(existingAccommodation));
        when(accommodationRepo.existsByHostIdAndTitle(1L, "Casa de Playa Actualizada")).thenReturn(false);
        when(accommodationRepo.save(any(Accomodation.class))).thenReturn(existingAccommodation);
        when(accommodationMapper.toDto(existingAccommodation)).thenReturn(expectedDTO);

        // Act
        AccommodationDTO result = accommodationService.updateAccommodation(1L, validUpdateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.title(), result.title());
        assertEquals(expectedDTO.maxGuestCapacity(), result.maxGuestCapacity());
        assertEquals(expectedDTO.numberOfBeds(), result.numberOfBeds());
        assertEquals(expectedDTO.numberOfBathrooms(), result.numberOfBathrooms());

        verify(accommodationRepo).findById(1L);
        verify(accommodationRepo).existsByHostIdAndTitle(1L, "Casa de Playa Actualizada");
        verify(accommodationMapper).updateEntityFromDTO(validUpdateDTO, existingAccommodation);
        verify(accommodationRepo).save(existingAccommodation);
    }

    //  Fracaso: Alojamiento no encontrado
    @Test
    void updateAccommodation_Failure_AccommodationNotFound() {
        // Arrange
        when(accommodationRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.updateAccommodation(1L, validUpdateDTO));

        assertEquals("Alojamiento no encontrado con ID: 1", exception.getMessage());
        verify(accommodationRepo).findById(1L);
        verify(accommodationRepo, never()).save(any(Accomodation.class));
    }
    //Datos inválidos: Título duplicado
    @Test
    void updateAccommodation_InvalidData_DuplicateTitle() {
        // Arrange
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(existingAccommodation));
        when(accommodationRepo.existsByHostIdAndTitle(1L, "Casa de Playa Actualizada")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> accommodationService.updateAccommodation(1L, validUpdateDTO));

        assertEquals("Ya existe un alojamiento con este título", exception.getMessage());
        verify(accommodationRepo).findById(1L);
        verify(accommodationRepo).existsByHostIdAndTitle(1L, "Casa de Playa Actualizada");
        verify(accommodationRepo, never()).save(any(Accomodation.class));
    }
    // Edge case: Mismo título (no debe lanzar excepción)
    @Test
    void updateAccommodation_EdgeCase_SameTitle() throws Exception {
        // Arrange
        AccommodationUpdateDTO sameTitleDTO = new AccommodationUpdateDTO(
                1L,
                "Casa de Playa Original", // Same title as existing
                "HOUSE",
                "No fumar, no mascotas",
                new UbicationCreateDTO("Calle 123", "Ciudad", 40.7128, -74.0060),
                4,
                2,
                1,
                1L,
                Arrays.asList("photo1.jpg", "photo2.jpg"),
                Arrays.asList(1L, 2L)
        );

        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(existingAccommodation));
        when(accommodationRepo.existsByHostIdAndTitle(1L, "Casa de Playa Original")).thenReturn(false);
        when(accommodationRepo.save(any(Accomodation.class))).thenReturn(existingAccommodation);
        when(accommodationMapper.toDto(existingAccommodation)).thenReturn(expectedDTO);

        // Act
        AccommodationDTO result = accommodationService.updateAccommodation(1L, sameTitleDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        verify(accommodationRepo).findById(1L);
        verify(accommodationRepo).existsByHostIdAndTitle(1L, "Casa de Playa Original");
        verify(accommodationRepo).save(existingAccommodation);
    }
}
