package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesAdmin;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.repository.user.AdminRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import com.gestion.alojamientos.service.Impl.ResetCodeServiceImpl;
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
 * Clase de prueba para el método generateResetCode del servicio AdminServiceImpl.
 * Prueba la funcionalidad de generar código de reseteo de contraseña.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGenerateResetCodeTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ResetCodeServiceImpl resetCodeServiceImpl;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin admin;
    private String validEmail;
    private String invalidEmail;
    private String resetCode;

    @BeforeEach
    void setUp() {
        validEmail = "admin@test.com";
        invalidEmail = "nonexistent@test.com";
        resetCode = "123456";
        
        admin = new Admin();
        admin.setId(1L);
        admin.setEmail(validEmail);
        admin.setUsername("admin");
        admin.setPassword("encodedPassword");
        admin.setAccess_level(1);
        admin.setStatesAdmin(StatesAdmin.ACTIVE);
        admin.setRole(Role.ADMIN);
    }

    /**
     * Prueba el caso de éxito: generar código de reseteo para un admin existente.
     * Verifica que se genere y envíe correctamente el código de reseteo.
     */
    @Test
    void shouldReturnResetCode_WhenAdminExists() throws ElementNotFoundException {
        // Given
        when(adminRepository.findByEmail(validEmail)).thenReturn(Optional.of(admin));
        when(resetCodeServiceImpl.generateAndSendCode(admin)).thenReturn(resetCode);

        // When
        String result = adminService.generateResetCode(validEmail);

        // Then
        assertNotNull(result);
        assertEquals(resetCode, result);
        verify(adminRepository).findByEmail(validEmail);
        verify(resetCodeServiceImpl).generateAndSendCode(admin);
    }

    /**
     * Prueba el caso de fracaso: cuando el admin no existe.
     * Verifica que se lance ElementNotFoundException cuando el email no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenAdminDoesNotExist() {
        // Given
        when(adminRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.generateResetCode(invalidEmail)
        );

        assertEquals("Huésped no encontrado con email: " + invalidEmail, exception.getMessage());
        verify(adminRepository).findByEmail(invalidEmail);
        verifyNoInteractions(resetCodeServiceImpl);
    }

    /**
     * Prueba el caso de datos inválidos: email nulo o vacío.
     * Verifica que se lance ElementNotFoundException cuando el email es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(adminRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> adminService.generateResetCode(null)
        );

        assertEquals("Huésped no encontrado con email: null", exception.getMessage());
        verify(adminRepository).findByEmail(null);
        verifyNoInteractions(resetCodeServiceImpl);
    }

    /**
     * Prueba el caso edge: cuando el servicio de reset code falla.
     * Verifica que se propague la excepción cuando el servicio de reset code falla.
     */
    @Test
    void shouldHandleEdgeCase_WhenResetCodeServiceFails() throws ElementNotFoundException {
        // Given
        when(adminRepository.findByEmail(validEmail)).thenReturn(Optional.of(admin));
        when(resetCodeServiceImpl.generateAndSendCode(admin)).thenThrow(new RuntimeException("Email service unavailable"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.generateResetCode(validEmail)
        );

        assertEquals("Email service unavailable", exception.getMessage());
        verify(adminRepository).findByEmail(validEmail);
        verify(resetCodeServiceImpl).generateAndSendCode(admin);
    }
}
