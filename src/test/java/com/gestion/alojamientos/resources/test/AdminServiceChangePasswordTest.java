package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.enums.Role;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método changePassword del servicio AdminServiceImpl.
 * Prueba la funcionalidad de cambiar contraseña de un admin.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceChangePasswordTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin admin;
    private ChangePasswordDto validChangePasswordDto;
    private ChangePasswordDto invalidCurrentPasswordDto;
    private ChangePasswordDto weakNewPasswordDto;
    private Long validUserId;
    private Long invalidUserId;

    @BeforeEach
    void setUp() {
        validUserId = 1L;
        invalidUserId = 99L;
        
        admin = new Admin();
        admin.setId(validUserId);
        admin.setEmail("admin@test.com");
        admin.setUsername("admin");
        admin.setPassword("$2a$10$encodedCurrentPassword");
        admin.setAccess_level(1);
        admin.setStatesAdmin(StatesAdmin.ACTIVE);

        validChangePasswordDto = new ChangePasswordDto(
                "currentPassword123",    // currentPassword
                "NewPassword123"         // newPassword
        );

        invalidCurrentPasswordDto = new ChangePasswordDto(
                "wrongPassword",         // currentPassword
                "NewPassword123"         // newPassword
        );

        weakNewPasswordDto = new ChangePasswordDto(
                "currentPassword123",    // currentPassword
                "weak"                   // newPassword (doesn't meet policy)
        );
    }

    /**
     * Prueba el caso de éxito: cambiar contraseña con datos válidos.
     * Verifica que se actualice correctamente la contraseña del admin.
     */
    @Test
    void shouldChangePasswordSuccessfully_WhenDataIsValid() throws ElementNotFoundException, InvalidElementException {
        // Given
        when(adminRepository.findById(validUserId)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("currentPassword123", admin.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("$2a$10$encodedNewPassword");

        // When
        adminService.changePassword(validUserId, validChangePasswordDto);

        // Then
        verify(adminRepository).findById(validUserId);
        verify(passwordEncoder).matches("currentPassword123", admin.getPassword());
        verify(passwordEncoder).encode("NewPassword123");
        verify(adminRepository).save(admin);
        assertEquals("$2a$10$encodedNewPassword", admin.getPassword());
    }

    /**
     * Prueba el caso de fracaso: cuando el admin no existe.
     * Verifica que se lance ElementNotFoundException cuando el ID no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenAdminDoesNotExist() {
        // Given
        when(adminRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.changePassword(invalidUserId, validChangePasswordDto)
        );

        assertEquals("Usuario no encontrado con ID: " + invalidUserId, exception.getMessage());
        verify(adminRepository).findById(invalidUserId);
        verifyNoInteractions(passwordEncoder);
    }

    /**
     * Prueba el caso de datos inválidos: contraseña actual incorrecta.
     * Verifica que se lance InvalidElementException cuando la contraseña actual es incorrecta.
     */
    @Test
    void shouldHandleInvalidData_WhenCurrentPasswordIsIncorrect() {
        // Given
        when(adminRepository.findById(validUserId)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongPassword", admin.getPassword())).thenReturn(false);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> adminService.changePassword(validUserId, invalidCurrentPasswordDto)
        );

        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(adminRepository).findById(validUserId);
        verify(passwordEncoder).matches("wrongPassword", admin.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(adminRepository, never()).save(any());
    }

    /**
     * Prueba el caso edge: cuando la nueva contraseña no cumple la política de seguridad.
     * Verifica que se lance InvalidElementException cuando la nueva contraseña es débil.
     */
    @Test
    void shouldHandleEdgeCase_WhenNewPasswordDoesNotMeetPolicy() {
        // Given
        when(adminRepository.findById(validUserId)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("currentPassword123", admin.getPassword())).thenReturn(true);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> adminService.changePassword(validUserId, weakNewPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple las politicas de privacidad", exception.getMessage());
        verify(adminRepository).findById(validUserId);
        verify(passwordEncoder).matches("currentPassword123", admin.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(adminRepository, never()).save(any());
    }
}
