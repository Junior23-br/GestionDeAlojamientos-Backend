package com.gestion.alojamientos.resources.test;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método deleteAccommodation del servicio AdminServiceImpl.
 * Prueba la funcionalidad de eliminar (soft delete) un alojamiento.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceDeleteAccommodationTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Accomodation accommodation;
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
    }

    /**
     * Prueba el caso de éxito: eliminar un alojamiento existente.
     * Verifica que se cambie el estado a DELETED y se guarde correctamente.
     */
    @Test
    void shouldDeleteAccommodationSuccessfully_WhenAccommodationExists() {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(accommodationRepo.save(any(Accomodation.class))).thenReturn(accommodation);

        // When
        adminService.deleteAccommodation(validAccommodationId);

        // Then
        verify(accommodationRepo).findById(validAccommodationId);
        verify(accommodationRepo).save(accommodation);
        assertEquals(OperationalStatus.DELETED, accommodation.getOperationalStatus());
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
                () -> adminService.deleteAccommodation(invalidAccommodationId)
        );

        assertEquals("Alojamiento no encontrado con ID: " + invalidAccommodationId, exception.getMessage());
        verify(accommodationRepo).findById(invalidAccommodationId);
        verify(accommodationRepo, never()).save(any());
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
                () -> adminService.deleteAccommodation(null)
        );

        assertEquals("Alojamiento no encontrado con ID: null", exception.getMessage());
        verify(accommodationRepo).findById(null);
        verify(accommodationRepo, never()).save(any());
    }

    /**
     * Prueba el caso edge: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando accommodationRepo es null.
     */
    @Test
    void shouldHandleEdgeCase_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → accommodationRepo = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.deleteAccommodation(validAccommodationId)
        );

        assertEquals("AccommodationRepo no está disponible (DELETE ACCOMMODATION).", exception.getMessage());
    }
}
