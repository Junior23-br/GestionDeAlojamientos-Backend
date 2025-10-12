package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.CalificationAccommodation.AccommodationCalificationDTO;
import com.gestion.alojamientos.mapper.accomodation.AccommodationCalificationMapper;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationCalification;
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
 * Clase de prueba para el método getHostAccommodationCalificationHistory del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener el historial de calificaciones de alojamientos de un host.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetHostAccommodationCalificationHistoryTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private AccommodationCalificationMapper accommodationCalificationMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Long validHostId;
    private Accomodation accommodation1;
    private Accomodation accommodation2;
    private AccomodationCalification calification1;
    private AccomodationCalification calification2;
    private AccommodationCalificationDTO calificationDTO1;
    private AccommodationCalificationDTO calificationDTO2;
    private List<Accomodation> accommodations;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        
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

        calification1 = new AccomodationCalification();
        calification1.setId(1L);
        calification1.setCleanliness(5);
        calification1.setComfort(4);
        calification1.setLocation(5);
        calification1.setAccuracyOfListing(4);
        calification1.setCommunicationHost(5);
        calification1.setProm(4.6);

        calification2 = new AccomodationCalification();
        calification2.setId(2L);
        calification2.setCleanliness(4);
        calification2.setComfort(5);
        calification2.setLocation(4);
        calification2.setAccuracyOfListing(5);
        calification2.setCommunicationHost(4);
        calification2.setProm(4.4);

        accommodation1.setAccomodationCalificationList(Arrays.asList(calification1));
        accommodation2.setAccomodationCalificationList(Arrays.asList(calification2));

        calificationDTO1 = new AccommodationCalificationDTO(
                1L, 5, 4, 5, 4, 1L, 5, 4.6
        );

        calificationDTO2 = new AccommodationCalificationDTO(
                2L, 4, 5, 4, 5, 2L, 4, 4.4
        );

        accommodations = Arrays.asList(accommodation1, accommodation2);
    }

    /**
     * Prueba el caso de éxito: obtener el historial de calificaciones de alojamientos.
     * Verifica que se retorne correctamente la lista de AccommodationCalificationDTO.
     */
    @Test
    void shouldReturnCalificationHistory_WhenCalificationsExist() {
        // Given
        when(accommodationRepo.findByHostId(validHostId)).thenReturn(accommodations);
        when(accommodationCalificationMapper.toDTO(calification1)).thenReturn(calificationDTO1);
        when(accommodationCalificationMapper.toDTO(calification2)).thenReturn(calificationDTO2);

        // When
        List<AccommodationCalificationDTO> result = adminService.getHostAccommodationCalificationHistory(validHostId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(calificationDTO1, result.get(0));
        assertEquals(calificationDTO2, result.get(1));
        verify(accommodationRepo).findByHostId(validHostId);
        verify(accommodationCalificationMapper).toDTO(calification1);
        verify(accommodationCalificationMapper).toDTO(calification2);
    }

    /**
     * Prueba el caso de fracaso: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando accommodationMapper es null.
     */
    @Test
    void shouldThrowUnsupportedOperationException_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → accommodationMapper = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.getHostAccommodationCalificationHistory(validHostId)
        );

        assertEquals("AccommodationMapper no está disponible (CALIFICATIONS).", exception.getMessage());
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se maneje correctamente cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(accommodationRepo.findByHostId(null)).thenReturn(Collections.emptyList());

        // When
        List<AccommodationCalificationDTO> result = adminService.getHostAccommodationCalificationHistory(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accommodationRepo).findByHostId(null);
        verifyNoInteractions(accommodationCalificationMapper);
    }

    /**
     * Prueba el caso edge: cuando no hay calificaciones para el host.
     * Verifica que se retorne una lista vacía cuando no existen calificaciones.
     */
    @Test
    void shouldHandleEdgeCase_WhenNoCalificationsExist() {
        // Given
        when(accommodationRepo.findByHostId(validHostId)).thenReturn(Collections.emptyList());

        // When
        List<AccommodationCalificationDTO> result = adminService.getHostAccommodationCalificationHistory(validHostId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accommodationRepo).findByHostId(validHostId);
        verifyNoInteractions(accommodationCalificationMapper);
    }
}
