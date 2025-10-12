package com.gestion.alojamientos.service;


import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.dto.admin.EditAdminDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;

import com.gestion.alojamientos.mapper.users.AdminMapper;
import com.gestion.alojamientos.model.enums.StatesAdmin;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.repository.user.AdminRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para el método editAdmin de AdminServiceImpl
 * Casos probados:
 *  1️⃣ Éxito – edición correcta
 *  2️⃣ Error – admin no encontrado
 *  3️⃣ Edge case – contraseña vacía o nula
 *  4️⃣ Error interno – excepción inesperada al guardar
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceImplEditAdminTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin existingAdmin;
    private EditAdminDto editDto;
    private Admin updatedAdmin;
    private AdminDto mappedDto;

    @BeforeEach
    void setUp() {
        existingAdmin = new Admin();
        existingAdmin.setId(1L);
        existingAdmin.setEmail("admin@test.com");
        existingAdmin.setPassword("oldPassword");
        existingAdmin.setUsername("adminUser");
        existingAdmin.setAccess_level(1);
        existingAdmin.setStatesAdmin(StatesAdmin.ACTIVE);

        editDto = new EditAdminDto(1L, "admin@test.com", "NewPassword123");

        updatedAdmin = new Admin();
        updatedAdmin.setId(1L);
        updatedAdmin.setEmail("admin@test.com");
        updatedAdmin.setPassword("encodedPassword");
        updatedAdmin.setUsername("adminUser");
        updatedAdmin.setAccess_level(1);
        updatedAdmin.setStatesAdmin(StatesAdmin.ACTIVE);

        mappedDto = new AdminDto(
                1L,
                updatedAdmin.getEmail(),
                updatedAdmin.getUsername(),
                updatedAdmin.getAccess_level(),
                updatedAdmin.getStatesAdmin().name(),
                null
        );
    }

    // ==========================================================
    // 1️⃣ Caso de ÉXITO
    // ==========================================================
    @Test
    @DisplayName("ÉXITO: debe editar correctamente el admin existente")
    void editAdmin_successfully() throws ElementNotFoundException {
        // Arrange
        when(adminRepository.findById(1L)).thenReturn(Optional.of(existingAdmin));
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encodedPassword");
        when(adminRepository.save(existingAdmin)).thenReturn(updatedAdmin);
        when(adminMapper.toDTO(updatedAdmin)).thenReturn(mappedDto);

        // Act
        AdminDto result = adminService.editAdmin(1L, editDto);

        // Assert
        assertNotNull(result);
        assertEquals("admin@test.com", result.email());
        assertEquals("adminUser", result.username());
        assertEquals("ACTIVE", result.statesAdmin());
        verify(adminRepository).findById(1L);
        verify(passwordEncoder).encode("NewPassword123");
        verify(adminRepository).save(existingAdmin);
        verify(adminMapper).toDTO(updatedAdmin);
    }

    // ==========================================================
    // 2️⃣ Caso de ERROR: Admin no encontrado
    // ==========================================================
    @Test
    @DisplayName("ERROR: debe lanzar excepción si el admin no existe")
    void editAdmin_notFound() {
        // Arrange
        when(adminRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.editAdmin(99L, editDto)
        );

        assertEquals("Administrador no encontrado con ID: 99", exception.getMessage());
        verify(adminRepository).findById(99L);
        verifyNoMoreInteractions(adminRepository, passwordEncoder, adminMapper);
    }

    // ==========================================================
    // 3️⃣ Edge Case: Contraseña vacía o nula
    // ==========================================================
    @Nested
    @DisplayName("Edge Cases de contraseña")
    class PasswordEdgeCases {

        @Test
        @DisplayName("Debe encriptar aunque la contraseña esté vacía (no lanza error)")
        void editAdmin_emptyPassword() throws ElementNotFoundException {
            // Arrange
            EditAdminDto emptyPasswordDto = new EditAdminDto(1L, "admin@test.com", "");
            when(adminRepository.findById(1L)).thenReturn(Optional.of(existingAdmin));
            when(passwordEncoder.encode("")).thenReturn("encodedEmpty");
            when(adminRepository.save(existingAdmin)).thenReturn(updatedAdmin);
            when(adminMapper.toDTO(updatedAdmin)).thenReturn(mappedDto);

            // Act
            AdminDto result = adminService.editAdmin(1L, emptyPasswordDto);

            // Assert
            assertNotNull(result);
            verify(passwordEncoder).encode("");
        }

        @Test
        @DisplayName("Debe lanzar NullPointerException si la contraseña es nula")
        void editAdmin_nullPassword() {
            // Arrange
            EditAdminDto nullPasswordDto = new EditAdminDto(1L, "admin@test.com", null);
            when(adminRepository.findById(1L)).thenReturn(Optional.of(existingAdmin));

            // Act & Assert
            assertThrows(NullPointerException.class, () ->
                    adminService.editAdmin(1L, nullPasswordDto)
            );
        }
    }

    // ==========================================================
    // 4️⃣ Caso de ERROR interno: Falla al guardar en repositorio
    // ==========================================================
    @Test
    @DisplayName("ERROR INTERNO: debe propagar excepción al fallar el save()")
    void editAdmin_internalErrorOnSave() {
        // Arrange
        when(adminRepository.findById(1L)).thenReturn(Optional.of(existingAdmin));
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encodedPassword");
        when(adminRepository.save(existingAdmin)).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.editAdmin(1L, editDto)
        );

        assertEquals("DB error", exception.getMessage());
        verify(adminRepository).findById(1L);
        verify(passwordEncoder).encode("NewPassword123");
        verify(adminRepository).save(existingAdmin);
    }
}
