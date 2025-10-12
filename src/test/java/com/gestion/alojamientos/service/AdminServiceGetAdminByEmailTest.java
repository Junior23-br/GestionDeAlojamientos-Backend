package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.mapper.users.AdminMapper;
import com.gestion.alojamientos.model.enums.StatesAdmin;
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
 * Test class for AdminService getAdminByEmail method.
 * Covers success, not found, null/empty email, and mapper error scenarios.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetAdminByEmailTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin existingAdmin;
    private AdminDto expectedDto;
    private final String VALID_EMAIL = "admin@mail.com";
    private final String INVALID_EMAIL = "nonexistent@mail.com";

    @BeforeEach
    void setUp() {
        existingAdmin = new Admin();
        existingAdmin.setId(1L);
        existingAdmin.setEmail(VALID_EMAIL);
        existingAdmin.setUsername("AdminUser");
        existingAdmin.setAccess_level(5);
        existingAdmin.setStatesAdmin(StatesAdmin.ACTIVE);

        expectedDto = new AdminDto(
                existingAdmin.getId(),
                existingAdmin.getEmail(),
                existingAdmin.getUsername(),
                existingAdmin.getAccess_level(),
                existingAdmin.getStatesAdmin().toString(),
                null
        );
    }

    /**
     * Test 1: Éxito - Se obtiene el administrador por email correctamente
     */
    @Test
    void getAdminByEmail_Success_ShouldReturnAdminDto() throws ElementNotFoundException {
        // Arrange
        when(adminRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingAdmin));
        when(adminMapper.toDTO(existingAdmin)).thenReturn(expectedDto);

        // Act
        AdminDto result = adminService.getAdminByEmail(VALID_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.email(), result.email());
        assertEquals(expectedDto.username(), result.username());
        verify(adminRepository).findByEmail(VALID_EMAIL);
        verify(adminMapper).toDTO(existingAdmin);
    }

    /**
     * Test 2: Fracaso - Administrador no encontrado por email
     */
    @Test
    void getAdminByEmail_NotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(adminRepository.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.getAdminByEmail(INVALID_EMAIL)
        );

        assertEquals("Administrador no encontrado con email: " + INVALID_EMAIL, exception.getMessage());
        verify(adminRepository).findByEmail(INVALID_EMAIL);
        verify(adminMapper, never()).toDTO(any());
    }

    /**
     * Test 3: Caso edge - Email nulo o vacío
     */
    @Test
    void getAdminByEmail_NullOrEmptyEmail_ShouldThrowElementNotFoundException() {
        // Act & Assert
        ElementNotFoundException ex1 = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.getAdminByEmail(null)
        );

        ElementNotFoundException ex2 = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.getAdminByEmail("")
        );

        assertTrue(ex1.getMessage().contains("Administrador no encontrado"));
        assertTrue(ex2.getMessage().contains("Administrador no encontrado"));
        verify(adminRepository, never()).findByEmail(any());
        verify(adminMapper, never()).toDTO(any());
    }

    /**
     * Test 4: Error interno - Mapper lanza excepción
     */
    @Test
    void getAdminByEmail_MapperThrowsException_ShouldPropagateRuntimeException() {
        // Arrange
        when(adminRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingAdmin));
        when(adminMapper.toDTO(existingAdmin)).thenThrow(new RuntimeException("Error en el mapper"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.getAdminByEmail(VALID_EMAIL)
        );

        assertTrue(exception.getMessage().contains("Error en el mapper"));
        verify(adminRepository).findByEmail(VALID_EMAIL);
        verify(adminMapper).toDTO(existingAdmin);
    }
}
