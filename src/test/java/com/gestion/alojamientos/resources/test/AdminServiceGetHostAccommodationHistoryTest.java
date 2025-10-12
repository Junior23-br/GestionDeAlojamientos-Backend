package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.dto.accommodation.ServiceDTO;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationType;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getHostAccommodationHistory del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener el historial de alojamientos de un host.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetHostAccommodationHistoryTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private AccommodationMapper accommodationMapper;

    @Mock
    private HostRepo hostRepo;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Long validHostId;
    private Long invalidHostId;
    private Accomodation accommodation1;
    private Accomodation accommodation2;
    private AccommodationDTO accommodationDTO1;
    private AccommodationDTO accommodationDTO2;
    private List<Accomodation> accommodations;
    private List<AccommodationDTO> accommodationDTOs;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        invalidHostId = 99L;
        
        accommodation1 = new Accomodation();
        accommodation1.setId(1L);
        accommodation1.setTitle("Casa en la playa");
        accommodation1.setAccomodationType(AccomodationType.HOUSE);
        accommodation1.setMaxGuestCapacity(4);
        accommodation1.setNumberOfBeds(2);
        accommodation1.setNumberOfBathrooms(1);
        accommodation1.setApprovalStatus(ApprovalStatus.APPROVED);
        accommodation1.setOperationalStatus(OperationalStatus.ACTIVE);
        accommodation1.setCreatedTime(LocalDateTime.now());
        accommodation1.setUpdateTime(LocalDateTime.now());

        accommodation2 = new Accomodation();
        accommodation2.setId(2L);
        accommodation2.setTitle("Apartamento en el centro");
        accommodation2.setAccomodationType(AccomodationType.APARTMENT);
        accommodation2.setMaxGuestCapacity(2);
        accommodation2.setNumberOfBeds(1);
        accommodation2.setNumberOfBathrooms(1);
        accommodation2.setApprovalStatus(ApprovalStatus.APPROVED);
        accommodation2.setOperationalStatus(OperationalStatus.ACTIVE);
        accommodation2.setCreatedTime(LocalDateTime.now());
        accommodation2.setUpdateTime(LocalDateTime.now());

        accommodationDTO1 = new AccommodationDTO(
                1L, "Casa en la playa", "HOUSE", "No fumar", 1L, 4, 2, 1,
                "APPROVED", "ACTIVE", LocalDateTime.now(), LocalDateTime.now(), 1L,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()
        );

        accommodationDTO2 = new AccommodationDTO(
                2L, "Apartamento en el centro", "APARTMENT", "No mascotas", 2L, 2, 1, 1,
                "APPROVED", "ACTIVE", LocalDateTime.now(), LocalDateTime.now(), 1L,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()
        );

        accommodations = Arrays.asList(accommodation1, accommodation2);
        accommodationDTOs = Arrays.asList(accommodationDTO1, accommodationDTO2);
    }

    /**
     * Prueba el caso de éxito: obtener el historial de alojamientos de un host existente.
     * Verifica que se retorne correctamente la lista de AccommodationDTO.
     */
    @Test
    void shouldReturnAccommodationHistory_WhenHostExists() {
        // Given
        when(hostRepo.existsById(validHostId)).thenReturn(true);
        when(accommodationRepo.findByHostId(validHostId)).thenReturn(accommodations);
        when(accommodationMapper.toDto(accommodation1)).thenReturn(accommodationDTO1);
        when(accommodationMapper.toDto(accommodation2)).thenReturn(accommodationDTO2);

        // When
        List<AccommodationDTO> result = adminService.getHostAccommodationHistory(validHostId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(accommodationDTO1, result.get(0));
        assertEquals(accommodationDTO2, result.get(1));
        verify(hostRepo).existsById(validHostId);
        verify(accommodationRepo).findByHostId(validHostId);
        verify(accommodationMapper).toDto(accommodation1);
        verify(accommodationMapper).toDto(accommodation2);
    }

    /**
     * Prueba el caso de fracaso: cuando el host no existe.
     * Verifica que se lance EntityNotFoundException cuando el host no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenHostDoesNotExist() {
        // Given
        when(hostRepo.existsById(invalidHostId)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getHostAccommodationHistory(invalidHostId)
        );

        assertEquals("Host no encontrado con ID: " + invalidHostId, exception.getMessage());
        verify(hostRepo).existsById(invalidHostId);
        verify(accommodationRepo, never()).findByHostId(any());
        verifyNoInteractions(accommodationMapper);
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se lance EntityNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(hostRepo.existsById(null)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getHostAccommodationHistory(null)
        );

        assertEquals("Host no encontrado con ID: null", exception.getMessage());
        verify(hostRepo).existsById(null);
        verify(accommodationRepo, never()).findByHostId(any());
        verifyNoInteractions(accommodationMapper);
    }

    /**
     * Prueba el caso edge: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando accommodationRepo o accommodationMapper son null.
     */
    @Test
    void shouldHandleEdgeCase_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → accommodationRepo y accommodationMapper = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.getHostAccommodationHistory(validHostId)
        );

        assertEquals("AccommodationRepo o AccommodationMapper no están disponibles (HOST ACCOMMODATIONS).", exception.getMessage());
    }
}
