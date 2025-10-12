package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationType;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
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
 * Clase de prueba para el método getAllAccommodations del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener todos los alojamientos.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetAllAccommodationsTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private AccommodationMapper accommodationMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Accomodation accommodation1;
    private Accomodation accommodation2;
    private AccommodationDTO accommodationDTO1;
    private AccommodationDTO accommodationDTO2;
    private List<Accomodation> accommodations;
    private List<AccommodationDTO> accommodationDTOs;

    @BeforeEach
    void setUp() {
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
     * Prueba el caso de éxito: obtener todos los alojamientos.
     * Verifica que se retorne correctamente la lista de AccommodationDTO.
     */
    @Test
    void shouldReturnAllAccommodations_WhenAccommodationsExist() {
        // Given
        when(accommodationRepo.findAll()).thenReturn(accommodations);
        when(accommodationMapper.toDto(accommodation1)).thenReturn(accommodationDTO1);
        when(accommodationMapper.toDto(accommodation2)).thenReturn(accommodationDTO2);

        // When
        List<AccommodationDTO> result = adminService.getAllAccommodations();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(accommodationDTO1, result.get(0));
        assertEquals(accommodationDTO2, result.get(1));
        verify(accommodationRepo).findAll();
        verify(accommodationMapper).toDto(accommodation1);
        verify(accommodationMapper).toDto(accommodation2);
    }

    /**
     * Prueba el caso de fracaso: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando accommodationRepo o accommodationMapper son null.
     */
    @Test
    void shouldThrowUnsupportedOperationException_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → accommodationRepo y accommodationMapper = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.getAllAccommodations()
        );

        assertEquals("AccommodationRepo o AccommodationMapper no están disponibles (GET ALL ACCOMMODATIONS).", exception.getMessage());
    }

    /**
     * Prueba el caso de datos inválidos: cuando el repositorio retorna null.
     * Verifica que se maneje correctamente cuando el repositorio retorna null.
     */
    @Test
    void shouldHandleInvalidData_WhenRepositoryReturnsNull() {
        // Given
        when(accommodationRepo.findAll()).thenReturn(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> adminService.getAllAccommodations());
        verify(accommodationRepo).findAll();
        verifyNoInteractions(accommodationMapper);
    }

    /**
     * Prueba el caso edge: cuando no hay alojamientos en el sistema.
     * Verifica que se retorne una lista vacía cuando no existen alojamientos.
     */
    @Test
    void shouldHandleEdgeCase_WhenNoAccommodationsExist() {
        // Given
        when(accommodationRepo.findAll()).thenReturn(Collections.emptyList());

        // When
        List<AccommodationDTO> result = adminService.getAllAccommodations();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accommodationRepo).findAll();
        verifyNoInteractions(accommodationMapper);
    }
}
