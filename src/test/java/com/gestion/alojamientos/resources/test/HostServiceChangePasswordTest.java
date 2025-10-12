package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.HostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método changePassword del servicio HostServiceImpl.
 * Prueba la funcionalidad de cambiar contraseña de un host.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceChangePasswordTest {

    @Mock
    private HostRepo hostRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private HostServiceImpl hostService;

    private Host host;
    private ChangePasswordDto validChangePasswordDto;
    private ChangePasswordDto invalidCurrentPasswordDto;
    private ChangePasswordDto weakNewPasswordDto;
    private Long validUserId;
    private Long invalidUserId;

    @BeforeEach
    void setUp() {
        validUserId = 1L;
        invalidUserId = 99L;
        
        host = new Host();
        host.setId(validUserId);
        host.setEmail("juan@test.com");
        host.setUsername("juan@test.com");
        host.setName("Juan Pérez");
        host.setPhoneNumber("+571234567890");
        host.setBirthDate(null);
        host.setPersonalDescription("Descripción personal");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setRole(Role.HOST);
        host.setPassword("$2a$10$encodedCurrentPassword");

        validChangePasswordDto = new ChangePasswordDto(
                "currentPassword123",            // currentPassword
                "NewPassword123"                 // newPassword
        );

        invalidCurrentPasswordDto = new ChangePasswordDto(
                "wrongPassword",                 // currentPassword
                "NewPassword123"                 // newPassword
        );

        weakNewPasswordDto = new ChangePasswordDto(
                "currentPassword123",            // currentPassword
                "weak"                           // newPassword (doesn't meet policy)
        );
    }

    /**
     * Prueba el caso de éxito: cambiar contraseña con datos válidos.
     * Verifica que se actualice correctamente la contraseña del host.
     */
    @Test
    void shouldChangePasswordSuccessfully_WhenDataIsValid() throws ElementNotFoundException, InvalidElementException {
        // Given
        when(hostRepository.findById(validUserId)).thenReturn(Optional.of(host));
        when(passwordEncoder.matches("currentPassword123", host.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("$2a$10$encodedNewPassword");

        // When
        hostService.changePassword(validUserId, validChangePasswordDto);

        // Then
        verify(hostRepository).findById(validUserId);
        verify(passwordEncoder).matches("currentPassword123", host.getPassword());
        verify(passwordEncoder).encode("NewPassword123");
        verify(hostRepository).save(host);
        assertEquals("$2a$10$encodedNewPassword", host.getPassword());
    }

    /**
     * Prueba el caso de fracaso: cuando el host no existe.
     * Verifica que se lance ElementNotFoundException cuando el ID no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenHostDoesNotExist() {
        // Given
        when(hostRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.changePassword(invalidUserId, validChangePasswordDto)
        );

        assertEquals("Usuario no encontrado con ID: " + invalidUserId, exception.getMessage());
        verify(hostRepository).findById(invalidUserId);
        verifyNoInteractions(passwordEncoder);
    }

    /**
     * Prueba el caso de datos inválidos: contraseña actual incorrecta.
     * Verifica que se lance InvalidElementException cuando la contraseña actual es incorrecta.
     */
    @Test
    void shouldHandleInvalidData_WhenCurrentPasswordIsIncorrect() {
        // Given
        when(hostRepository.findById(validUserId)).thenReturn(Optional.of(host));
        when(passwordEncoder.matches("wrongPassword", host.getPassword())).thenReturn(false);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> hostService.changePassword(validUserId, invalidCurrentPasswordDto)
        );

        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(hostRepository).findById(validUserId);
        verify(passwordEncoder).matches("wrongPassword", host.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(hostRepository, never()).save(any());
    }

    /**
     * Prueba el caso edge: cuando la nueva contraseña no cumple la política de seguridad.
     * Verifica que se lance InvalidElementException cuando la nueva contraseña es débil.
     */
    @Test
    void shouldHandleEdgeCase_WhenNewPasswordDoesNotMeetPolicy() {
        // Given
        when(hostRepository.findById(validUserId)).thenReturn(Optional.of(host));
        when(passwordEncoder.matches("currentPassword123", host.getPassword())).thenReturn(true);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> hostService.changePassword(validUserId, weakNewPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple las politicas de privacidad", exception.getMessage());
        verify(hostRepository).findById(validUserId);
        verify(passwordEncoder).matches("currentPassword123", host.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(hostRepository, never()).save(any());
    }
}
