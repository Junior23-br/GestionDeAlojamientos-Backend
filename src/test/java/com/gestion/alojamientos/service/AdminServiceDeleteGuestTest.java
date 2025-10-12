package com.gestion.alojamientos.service;

import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for AdminServiceImpl.deleteGuest method.
 * Covers success, not found, repository null, and save failure scenarios.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceDeleteGuestTest {

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Guest existingGuest;

    @BeforeEach
    void setUp() {
        existingGuest = new Guest();
        existingGuest.setId(1L);
        existingGuest.setEmail("guest@mail.com");
        existingGuest.setState(StatesOfGuest.ACTIVE);
    }

    /**
     * Test 1: Éxito - Cambia el estado del huésped a DELETED correctamente
     */
    @Test
    void deleteGuest_Success_ShouldSetStateToDeletedAndSave() {
        // Arrange
        when(guestRepository.findById(1L)).thenReturn(Optional.of(existingGuest));

        // Act
        adminService.deleteGuest(1L);

        // Assert
        assertEquals(StatesOfGuest.DELETED, existingGuest.getState());
        verify(guestRepository).findById(1L);
        verify(guestRepository).save(existingGuest);
    }

    /**
     * Test 2: Fracaso - Huésped no encontrado por ID
     */
    @Test
    void deleteGuest_NotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(guestRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.deleteGuest(99L)
        );

        assertEquals("Huésped no encontrado con ID: 99", exception.getMessage());
        verify(guestRepository).findById(99L);
        verify(guestRepository, never()).save(any());
    }

    /**
     * Test 3: Repositorio nulo - Lanza UnsupportedOperationException
     */
    @Test
    void deleteGuest_NullRepository_ShouldThrowUnsupportedOperationException() {
        // Arrange
        AdminServiceImpl serviceWithNullRepo = new AdminServiceImpl();
        // Simulamos que guestRepository no está disponible
        // (no lo inyectamos manualmente)

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> serviceWithNullRepo.deleteGuest(1L)
        );

        assertTrue(exception.getMessage().contains("GuestRepository no está disponible"));
    }

    /**
     * Test 4: Error interno - Falla al guardar el huésped
     */
    @Test
    void deleteGuest_SaveThrowsException_ShouldPropagateRuntimeException() {
        // Arrange
        when(guestRepository.findById(1L)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.save(existingGuest)).thenThrow(new RuntimeException("Error al guardar"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.deleteGuest(1L)
        );

        assertTrue(exception.getMessage().contains("Error al guardar"));
        verify(guestRepository).findById(1L);
        verify(guestRepository).save(existingGuest);
    }
}
