package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.service.Impl.AccommodationServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceSoftDeleteAccommodationTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    private Accomodation testAccommodation;
    private Host testHost;

    @BeforeEach
    void setUp() {
        // Setup test host
        testHost = new Host();
        testHost.setId(1L);
        testHost.setName("Test Host");

        // Setup test accommodation
        testAccommodation = new Accomodation();
        testAccommodation.setId(1L);
        testAccommodation.setTitle("Casa de Playa");
        testAccommodation.setHost(testHost);
        testAccommodation.setApprovalStatus(ApprovalStatus.APPROVED);
        testAccommodation.setOperationalStatus(OperationalStatus.ACTIVE);
        testAccommodation.setCreatedTime(LocalDateTime.now().minusDays(30));
        testAccommodation.setUpdateTime(LocalDateTime.now().minusDays(1));
    }
    //  Éxito: Eliminación lógica exitosa
    @Test
    void softDeleteAccommodation_Success() throws Exception {
        // Arrange
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(testAccommodation));
        when(accommodationRepo.save(any(Accomodation.class))).thenReturn(testAccommodation);

        // Act
        boolean result = accommodationService.softDeleteAccommodation(1L, 1L);

        // Assert
        assertTrue(result);
        assertEquals(OperationalStatus.DELETED, testAccommodation.getOperationalStatus());
        assertNotNull(testAccommodation.getUpdateTime());

        verify(accommodationRepo).findById(1L);
        verify(accommodationRepo).save(testAccommodation);
    }
    // Fracaso: Alojamiento no encontrado
    @Test
    void softDeleteAccommodation_Failure_AccommodationNotFound() {
        // Arrange
        when(accommodationRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.softDeleteAccommodation(1L, 1L));

        assertEquals("Alojamiento no encontrado", exception.getMessage());
        verify(accommodationRepo).findById(1L);
        verify(accommodationRepo, never()).save(any(Accomodation.class));
    }
    // Datos inválidos: Host ID incorrecto (sin permisos)
    @Test
    void softDeleteAccommodation_InvalidData_WrongHostId() {
        // Arrange
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(testAccommodation));

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> accommodationService.softDeleteAccommodation(1L, 999L)); // Different host ID

        assertEquals("No tienes permisos para eliminar este alojamiento", exception.getMessage());
        verify(accommodationRepo).findById(1L);
        verify(accommodationRepo, never()).save(any(Accomodation.class));
    }
    // Edge case: Alojamiento ya eliminado
    @Test
    void softDeleteAccommodation_EdgeCase_AlreadyDeleted() throws Exception {
        // Arrange
        testAccommodation.setOperationalStatus(OperationalStatus.DELETED);
        when(accommodationRepo.findById(1L)).thenReturn(Optional.of(testAccommodation));
        when(accommodationRepo.save(any(Accomodation.class))).thenReturn(testAccommodation);

        // Act
        boolean result = accommodationService.softDeleteAccommodation(1L, 1L);

        // Assert
        assertTrue(result);
        assertEquals(OperationalStatus.DELETED, testAccommodation.getOperationalStatus());
        assertNotNull(testAccommodation.getUpdateTime());

        verify(accommodationRepo).findById(1L);
        verify(accommodationRepo).save(testAccommodation);
    }
}
