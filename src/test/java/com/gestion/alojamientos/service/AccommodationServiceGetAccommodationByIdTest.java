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
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.accomodation.Services;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.service.Impl.AccommodationServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceGetAccommodationByIdTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private AccommodationMapper accommodationMapper;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    private Accomodation testAccommodation;
    private AccommodationDTO expectedDTO;

    @BeforeEach
    void setUp() {
        // Setup test accommodation
        testAccommodation = new Accomodation();
        testAccommodation.setId(1L);
        testAccommodation.setTitle("Casa de Playa");
        testAccommodation.setApprovalStatus(ApprovalStatus.APPROVED);
        testAccommodation.setOperationalStatus(OperationalStatus.ACTIVE);
        testAccommodation.setCreatedTime(LocalDateTime.now());
        testAccommodation.setUpdateTime(LocalDateTime.now());
        testAccommodation.setUrlPhotos(Arrays.asList("photo1.jpg", "photo2.jpg"));

        // Setup services
        Services service1 = new Services();
        service1.setId(1L);
        service1.setName("WiFi");

        Services service2 = new Services();
        service2.setId(2L);
        service2.setName("Piscina");

        testAccommodation.setServicesList(Arrays.asList(service1, service2));

        // Setup expected DTO
        expectedDTO = new AccommodationDTO(
                1L,
                "Casa de Playa",
                "HOUSE",
                "No fumar, no mascotas",
                1L,
                4,
                2,
                1,
                "APPROVED",
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                null,
                null,
                null,
                Arrays.asList("photo1.jpg", "photo2.jpg"),
                null
        );
    }

    @Test
    void getAccommodationById_Success() {
        // Arrange
        when(accommodationRepo.findBaseById(1L)).thenReturn(Optional.of(testAccommodation));
        when(accommodationRepo.findByIdWithPhotos(1L)).thenReturn(Optional.of(testAccommodation));
        when(accommodationMapper.toDto(testAccommodation)).thenReturn(expectedDTO);

        // Act
        AccommodationDTO result = accommodationService.getAccommodationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.title(), result.title());
        assertEquals(expectedDTO.approvalStatus(), result.approvalStatus());
        assertEquals(expectedDTO.operationalStatus(), result.operationalStatus());

        verify(accommodationRepo).findBaseById(1L);
        verify(accommodationRepo, times(2)).findByIdWithPhotos(1L);
        verify(accommodationMapper).toDto(testAccommodation);
    }

    @Test
    void getAccommodationById_Failure_AccommodationNotFound() {
        // Arrange
        when(accommodationRepo.findBaseById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.getAccommodationById(1L));

        assertEquals("Alojamiento no encontrado con ID: 1", exception.getMessage());
        verify(accommodationRepo).findBaseById(1L);
        verify(accommodationMapper, never()).toDto(any(Accomodation.class));
    }

    @Test
    void getAccommodationById_InvalidData_NegativeId() {
        // Arrange
        when(accommodationRepo.findBaseById(-1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.getAccommodationById(-1L));

        assertEquals("Alojamiento no encontrado con ID: -1", exception.getMessage());
        verify(accommodationRepo).findBaseById(-1L);
        verify(accommodationMapper, never()).toDto(any(Accomodation.class));
    }

    @Test
    void getAccommodationById_EdgeCase_ZeroId() {
        // Arrange
        when(accommodationRepo.findBaseById(0L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.getAccommodationById(0L));

        assertEquals("Alojamiento no encontrado con ID: 0", exception.getMessage());
        verify(accommodationRepo).findBaseById(0L);
        verify(accommodationMapper, never()).toDto(any(Accomodation.class));
    }
}