package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationType;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getAccommodationById del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener un alojamiento por su ID.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetAccommodationByIdTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private AccommodationMapper accommodationMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Accomodation accommodation;
    private AccommodationDTO accommodationDTO;
    private Long validAccommodationId;
    private Long invalidAccommodationId;

    @BeforeEach
    void setUp() {
        validAccommodationId = 1L;
        invalidAccommodationId = 99L;
        
        accommodation = new Accomodation();
        accommodation.setId(validAccommodationId);
        accommodation.setTitle("Casa en la playa");
        accommodation.setAccomodationType(AccomodationType.HOUSE);
        accommodation.setMaxGuestCapacity(4);
        accommodation.setNumberOfBeds(2);
        accommodation.setNumberOfBathrooms(1);
        accommodation.setApprovalStatus(ApprovalStatus.APPROVED);
        accommodation.setOperationalStatus(OperationalStatus.ACTIVE);
        accommodation.setCreatedTime(LocalDateTime.now());
        accommodation.setUpdateTime(LocalDateTime.now());

        accommodationDTO = new AccommodationDTO(
                validAccommodationId, "Casa en la playa", "HOUSE", "No fumar", 1L, 4, 2, 1,
                "APPROVED", "ACTIVE", LocalDateTime.now(), LocalDateTime.now(), 1L,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()
        );
    }

    /**
     * Prueba el caso de éxito: obtener un alojamiento existente por ID.
     * Verifica que se retorne correctamente el AccommodationDTO cuando el alojamiento existe.
     */
    @Test
    void shouldReturnAccommodationDTO_WhenAccommodationExists() {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(accommodationDTO);

        // When
        AccommodationDTO result = adminService.getAccommodationById(validAccommodationId);

        // Then
        assertNotNull(result);
        assertEquals(validAccommodationId, result.id());
        assertEquals("Casa en la playa", result.title());
        assertEquals("HOUSE", result.accomodationType());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(accommodationMapper).toDto(accommodation);
    }

    /**
     * Prueba el caso de fracaso: cuando el alojamiento no existe.
     * Verifica que se lance EntityNotFoundException cuando el ID no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenAccommodationDoesNotExist() {
        // Given
        when(accommodationRepo.findById(invalidAccommodationId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getAccommodationById(invalidAccommodationId)
        );

        assertEquals("Alojamiento no encontrado con ID: " + invalidAccommodationId, exception.getMessage());
        verify(accommodationRepo).findById(invalidAccommodationId);
        verifyNoInteractions(accommodationMapper);
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se lance EntityNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(accommodationRepo.findById(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getAccommodationById(null)
        );

        assertEquals("Alojamiento no encontrado con ID: null", exception.getMessage());
        verify(accommodationRepo).findById(null);
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
                () -> service.getAccommodationById(validAccommodationId)
        );

        assertEquals("AccommodationRepo o AccommodationMapper no están disponibles (GET ACCOMMODATION BY ID).", exception.getMessage());
    }
}
