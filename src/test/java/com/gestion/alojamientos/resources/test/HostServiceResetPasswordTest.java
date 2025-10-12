package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.HostServiceImpl;
import com.gestion.alojamientos.service.Impl.ResetCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método resetPassword del servicio HostServiceImpl.
 * Prueba la funcionalidad de restablecer contraseña mediante código de verificación.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceResetPasswordTest {

    @Mock
    private HostRepo hostRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ResetCodeServiceImpl resetCodeServiceImpl;

    @InjectMocks
    private HostServiceImpl hostService;

    private Host host;
    private ResetPasswordDto validResetPasswordDto;
    private ResetPasswordDto invalidEmailResetPasswordDto;
    private ResetPasswordDto expiredCodeResetPasswordDto;
    private ResetPasswordDto weakPasswordResetPasswordDto;
    private String validEmail;
    private String invalidEmail;
    private String validResetCode;
    private String expiredResetCode;
    private String newPassword;

    @BeforeEach
    void setUp() {
        validEmail = "juan@test.com";
        invalidEmail = "nonexistent@test.com";
        validResetCode = "123456";
        expiredResetCode = "expired123";
        newPassword = "NewPassword123";
        
        host = new Host();
        host.setId(1L);
        host.setEmail(validEmail);
        host.setUsername(validEmail);
        host.setName("Juan Pérez");
        host.setPhoneNumber("+571234567890");
        host.setBirthDate(new Date());
        host.setPersonalDescription("Descripción personal");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setRole(Role.HOST);
        host.setPassword("$2a$10$oldEncodedPassword");
        host.setResetCode(validResetCode);

        validResetPasswordDto = new ResetPasswordDto(
                validEmail,                     // email
                validResetCode,                 // resetCode
                newPassword                     // newPassword
        );

        invalidEmailResetPasswordDto = new ResetPasswordDto(
                invalidEmail,                   // email
                validResetCode,                 // resetCode
                newPassword                     // newPassword
        );

        expiredCodeResetPasswordDto = new ResetPasswordDto(
                validEmail,                     // email
                expiredResetCode,               // resetCode
                newPassword                     // newPassword
        );

        weakPasswordResetPasswordDto = new ResetPasswordDto(
                validEmail,                     // email
                validResetCode,                 // resetCode
                "weak"                          // newPassword (doesn't meet policy)
        );
    }

    /**
     * Prueba el caso de éxito: restablecer contraseña con datos válidos.
     * Verifica que se actualice correctamente la contraseña del host.
     */
    @Test
    void shouldResetPasswordSuccessfully_WhenDataIsValid() throws ElementNotFoundException, InvalidElementException {
        // Given
        when(hostRepository.findByEmail(validEmail)).thenReturn(Optional.of(host));
        doNothing().when(resetCodeServiceImpl).validateCode(host, validResetCode);
        when(passwordEncoder.encode(newPassword)).thenReturn("$2a$10$newEncodedPassword");

        // When
        hostService.resetPassword(validResetPasswordDto);

        // Then
        verify(hostRepository).findByEmail(validEmail);
        verify(resetCodeServiceImpl).validateCode(host, validResetCode);
        verify(passwordEncoder).encode(newPassword);
        verify(hostRepository).save(host);
        assertEquals("$2a$10$newEncodedPassword", host.getPassword());
        assertNull(host.getResetCode());
    }

    /**
     * Prueba el caso de fracaso: cuando el host no existe.
     * Verifica que se lance ElementNotFoundException cuando el email no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenHostDoesNotExist() {
        // Given
        when(hostRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.resetPassword(invalidEmailResetPasswordDto)
        );

        assertEquals("Huésped no encontrado con email: " + invalidEmail, exception.getMessage());
        verify(hostRepository).findByEmail(invalidEmail);
        verifyNoInteractions(resetCodeServiceImpl);
        verifyNoInteractions(passwordEncoder);
    }

    /**
     * Prueba el caso de datos inválidos: código de reseteo expirado.
     * Verifica que se lance InvalidElementException cuando el código ha expirado.
     */
    @Test
    void shouldHandleInvalidData_WhenResetCodeIsExpired() throws ElementNotFoundException {
        // Given
        when(hostRepository.findByEmail(validEmail)).thenReturn(Optional.of(host));
        doThrow(new InvalidElementException("Código de recuperación expirado."))
                .when(resetCodeServiceImpl).validateCode(host, expiredResetCode);
        when(resetCodeServiceImpl.generateAndSendCode(host)).thenReturn("newCode123");

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> hostService.resetPassword(expiredCodeResetPasswordDto)
        );

        assertTrue(exception.getMessage().contains("El código ha expirado"));
        assertTrue(exception.getMessage().contains(validEmail));
        verify(hostRepository).findByEmail(validEmail);
        verify(resetCodeServiceImpl).validateCode(host, expiredResetCode);
        verify(resetCodeServiceImpl).generateAndSendCode(host);
        verifyNoInteractions(passwordEncoder);
    }

    /**
     * Prueba el caso edge: cuando la nueva contraseña no cumple la política de seguridad.
     * Verifica que se lance InvalidElementException cuando la nueva contraseña es débil.
     */
    @Test
    void shouldHandleEdgeCase_WhenNewPasswordDoesNotMeetPolicy() throws ElementNotFoundException {
        // Given
        when(hostRepository.findByEmail(validEmail)).thenReturn(Optional.of(host));
        doNothing().when(resetCodeServiceImpl).validateCode(host, validResetCode);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> hostService.resetPassword(weakPasswordResetPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple con la política de seguridad.", exception.getMessage());
        verify(hostRepository).findByEmail(validEmail);
        verify(resetCodeServiceImpl).validateCode(host, validResetCode);
        verify(passwordEncoder, never()).encode(anyString());
        verify(hostRepository, never()).save(any());
    }
}
