package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.AccommodationServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceGetHostAccommodationsTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private HostRepo hostRepo;

    @Mock
    private AccommodationMapper accommodationMapper;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    private Host testHost;
    private List<Accomodation> testAccommodations;
    private List<AccommodationDTO> expectedDTOs;

    @BeforeEach
    void setUp() {
        // Setup test host
        testHost = new Host();
        testHost.setId(1L);
        testHost.setName("Test Host");

        // Setup test accommodations
        Accomodation accommodation1 = new Accomodation();
        accommodation1.setId(1L);
        accommodation1.setTitle("Casa de Playa");
        accommodation1.setHost(testHost);
        accommodation1.setApprovalStatus(ApprovalStatus.APPROVED);
        accommodation1.setOperationalStatus(OperationalStatus.ACTIVE);
        accommodation1.setCreatedTime(LocalDateTime.now().minusDays(30));

        Accomodation accommodation2 = new Accomodation();
        accommodation2.setId(2L);
        accommodation2.setTitle("Apartamento Centro");
        accommodation2.setHost(testHost);
        accommodation2.setApprovalStatus(ApprovalStatus.PENDING);
        accommodation2.setOperationalStatus(OperationalStatus.ACTIVE);
        accommodation2.setCreatedTime(LocalDateTime.now().minusDays(15));

        testAccommodations = Arrays.asList(accommodation1, accommodation2);

        // Setup expected DTOs
        AccommodationDTO dto1 = new AccommodationDTO(
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
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                1L,
                null,
                null,
                null,
                Arrays.asList("photo1.jpg", "photo2.jpg"),
                null
        );

        AccommodationDTO dto2 = new AccommodationDTO(
                2L,
                "Apartamento Centro",
                "APARTMENT",
                "No fiestas",
                2L,
                2,
                1,
                1,
                "PENDING",
                "ACTIVE",
                LocalDateTime.now().minusDays(15),
                LocalDateTime.now(),
                1L,
                null,
                null,
                null,
                Arrays.asList("photo3.jpg"),
                null
        );

        expectedDTOs = Arrays.asList(dto1, dto2);
    }
    //  Éxito: Obtención exitosa de alojamientos del host
    @Test
    void getHostAccommodations_Success() {
        // Arrange
        when(hostRepo.existsById(1L)).thenReturn(true);
        when(accommodationRepo.findByHostId(1L)).thenReturn(testAccommodations);
        when(accommodationMapper.toDto(testAccommodations.get(0))).thenReturn(expectedDTOs.get(0));
        when(accommodationMapper.toDto(testAccommodations.get(1))).thenReturn(expectedDTOs.get(1));

        // Act
        List<AccommodationDTO> result = accommodationService.getHostAccommodations(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDTOs.get(0).id(), result.get(0).id());
        assertEquals(expectedDTOs.get(0).title(), result.get(0).title());
        assertEquals(expectedDTOs.get(1).id(), result.get(1).id());
        assertEquals(expectedDTOs.get(1).title(), result.get(1).title());

        verify(hostRepo).existsById(1L);
        verify(accommodationRepo).findByHostId(1L);
        verify(accommodationMapper, times(2)).toDto(any(Accomodation.class));
    }
    //  Fracaso: Host no encontrado
    @Test
    void getHostAccommodations_Failure_HostNotFound() {
        // Arrange
        when(hostRepo.existsById(1L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.getHostAccommodations(1L));

        assertEquals("Host no encontrado con ID: 1", exception.getMessage());
        verify(hostRepo).existsById(1L);
        verify(accommodationRepo, never()).findByHostId(anyLong());
        verify(accommodationMapper, never()).toDto(any(Accomodation.class));
    }
    // Datos inválidos: Host ID negativo
    @Test
    void getHostAccommodations_InvalidData_NegativeHostId() {
        // Arrange
        when(hostRepo.existsById(-1L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.getHostAccommodations(-1L));

        assertEquals("Host no encontrado con ID: -1", exception.getMessage());
        verify(hostRepo).existsById(-1L);
        verify(accommodationRepo, never()).findByHostId(anyLong());
        verify(accommodationMapper, never()).toDto(any(Accomodation.class));
    }
    //  Edge case: Lista vacía de alojamientos
    @Test
    void getHostAccommodations_EdgeCase_EmptyAccommodationsList() {
        // Arrange
        when(hostRepo.existsById(1L)).thenReturn(true);
        when(accommodationRepo.findByHostId(1L)).thenReturn(Arrays.asList());

        // Act
        List<AccommodationDTO> result = accommodationService.getHostAccommodations(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hostRepo).existsById(1L);
        verify(accommodationRepo).findByHostId(1L);
        verify(accommodationMapper, never()).toDto(any(Accomodation.class));
    }
}
