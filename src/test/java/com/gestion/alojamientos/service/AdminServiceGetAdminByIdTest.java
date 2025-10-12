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
 * Test class for AdminService getAdminById method.
 * Covers success, not found, null ID, and internal error cases.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetAdminByIdTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin existingAdmin;
    private AdminDto expectedDto;
    private final Long VALID_ID = 1L;
    private final Long INVALID_ID = 99L;
    private final Long NULL_ID = null;

    @BeforeEach
    void setUp() {
        existingAdmin = new Admin();
        existingAdmin.setId(VALID_ID);
        existingAdmin.setEmail("admin@mail.com");
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
     * Test 1: Éxito - Se obtiene el administrador correctamente
     */
    @Test
    void getAdminById_Success_ShouldReturnAdminDto() throws ElementNotFoundException {
        // Arrange
        when(adminRepository.findById(VALID_ID)).thenReturn(Optional.of(existingAdmin));
        when(adminMapper.toDTO(existingAdmin)).thenReturn(expectedDto);

        // Act
        AdminDto result = adminService.getAdminById(VALID_ID);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.email(), result.email());
        assertEquals(expectedDto.username(), result.username());
        verify(adminRepository).findById(VALID_ID);
        verify(adminMapper).toDTO(existingAdmin);
    }

    /**
     * Test 2: Fracaso - Administrador no encontrado
     */
    @Test
    void getAdminById_NotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(adminRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.getAdminById(INVALID_ID)
        );

        assertEquals("Administrador no encontrado con ID: " + INVALID_ID, exception.getMessage());
        verify(adminRepository).findById(INVALID_ID);
        verify(adminMapper, never()).toDTO(any());
    }

    /**
     * Test 3: Caso edge - ID nulo
     */
    @Test
    void getAdminById_NullId_ShouldThrowElementNotFoundException() {
        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.getAdminById(NULL_ID)
        );

        assertTrue(exception.getMessage().contains("Administrador no encontrado"));
        verify(adminRepository, never()).findById(any());
        verify(adminMapper, never()).toDTO(any());
    }

    /**
     * Test 4: Error interno - Falla en el mapper
     */
    @Test
    void getAdminById_MapperThrowsException_ShouldPropagateRuntimeException() {
        // Arrange
        when(adminRepository.findById(VALID_ID)).thenReturn(Optional.of(existingAdmin));
        when(adminMapper.toDTO(existingAdmin)).thenThrow(new RuntimeException("Mapper error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.getAdminById(VALID_ID)
        );

        assertTrue(exception.getMessage().contains("Mapper error"));
        verify(adminRepository).findById(VALID_ID);
        verify(adminMapper).toDTO(existingAdmin);
    }
}
