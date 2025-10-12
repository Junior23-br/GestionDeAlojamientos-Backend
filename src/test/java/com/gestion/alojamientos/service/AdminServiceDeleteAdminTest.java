package com.gestion.alojamientos.service;

import com.gestion.alojamientos.exception.ElementNotFoundException;

import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.repository.user.AdminRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;

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
 * Test class for AdminService deleteAdmin method.
 * Covers success, element not found, invalid data, and internal failure cases.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceDeleteAdminTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin existingAdmin;
    private final Long VALID_ID = 1L;
    private final Long INVALID_ID = 99L;
    private final Long NULL_ID = null;

    @BeforeEach
    void setUp() {
        existingAdmin = new Admin();
        existingAdmin.setId(VALID_ID);
        existingAdmin.setEmail("test.admin@email.com");
        existingAdmin.setPassword("encodedPassword");
    }

    /**
     * Test 1: Éxito - Eliminación exitosa del administrador
     */
    @Test
    void deleteAdmin_Success_ShouldDeleteAdmin() throws ElementNotFoundException {
        // Arrange
        when(adminRepository.findById(VALID_ID)).thenReturn(Optional.of(existingAdmin));
        doNothing().when(adminRepository).delete(existingAdmin);

        // Act & Assert
        assertDoesNotThrow(() -> adminService.deleteAdmin(VALID_ID));

        // Verify
        verify(adminRepository).findById(VALID_ID);
        verify(adminRepository).delete(existingAdmin);
    }

    /**
     * Test 2: Fracaso - Administrador no encontrado
     */
    @Test
    void deleteAdmin_NotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(adminRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.deleteAdmin(INVALID_ID)
        );

        assertEquals("Administrador no encontrado con ID: " + INVALID_ID, exception.getMessage());
        verify(adminRepository).findById(INVALID_ID);
        verify(adminRepository, never()).delete(any(Admin.class));
    }

    /**
     * Test 3: Caso edge - ID nulo
     */
    @Test
    void deleteAdmin_NullId_ShouldThrowElementNotFoundException() {
        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.deleteAdmin(NULL_ID)
        );

        assertTrue(exception.getMessage().contains("Administrador no encontrado"));
        verify(adminRepository, never()).findById(any());
        verify(adminRepository, never()).delete((Admin) any());
    }

    /**
     * Test 4: Error interno - Falla al eliminar (por ejemplo, error en base de datos)
     */
    @Test
    void deleteAdmin_RepositoryError_ShouldThrowRuntimeException() {
        // Arrange
        when(adminRepository.findById(VALID_ID)).thenReturn(Optional.of(existingAdmin));
        doThrow(new RuntimeException("Database error")).when(adminRepository).delete(existingAdmin);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.deleteAdmin(VALID_ID)
        );

        assertTrue(exception.getMessage().contains("Database error"));
        verify(adminRepository).findById(VALID_ID);
        verify(adminRepository).delete(existingAdmin);
    }
}
