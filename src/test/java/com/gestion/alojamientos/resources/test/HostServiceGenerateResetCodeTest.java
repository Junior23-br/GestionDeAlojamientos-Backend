package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.exception.ElementNotFoundException;
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

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método generateResetCode del servicio HostServiceImpl.
 * Prueba la funcionalidad de generar código de reseteo de contraseña.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceGenerateResetCodeTest {

    @Mock
    private HostRepo hostRepository;

    @Mock
    private ResetCodeServiceImpl resetCodeServiceImpl;

    @InjectMocks
    private HostServiceImpl hostService;

    private Host host;
    private String validEmail;
    private String invalidEmail;
    private String resetCode;

    @BeforeEach
    void setUp() {
        validEmail = "juan@test.com";
        invalidEmail = "nonexistent@test.com";
        resetCode = "123456";
        
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
    }

    /**
     * Prueba el caso de éxito: generar código de reseteo para un host existente.
     * Verifica que se genere y envíe correctamente el código de reseteo.
     */
    @Test
    void shouldReturnResetCode_WhenHostExists() throws ElementNotFoundException {
        // Given
        when(hostRepository.findByEmail(validEmail)).thenReturn(Optional.of(host));
        when(resetCodeServiceImpl.generateAndSendCode(host)).thenReturn(resetCode);

        // When
        String result = hostService.generateResetCode(validEmail);

        // Then
        assertNotNull(result);
        assertEquals(resetCode, result);
        verify(hostRepository).findByEmail(validEmail);
        verify(resetCodeServiceImpl).generateAndSendCode(host);
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
                () -> hostService.generateResetCode(invalidEmail)
        );

        assertEquals("Huésped no encontrado con email: " + invalidEmail, exception.getMessage());
        verify(hostRepository).findByEmail(invalidEmail);
        verifyNoInteractions(resetCodeServiceImpl);
    }

    /**
     * Prueba el caso de datos inválidos: email nulo o vacío.
     * Verifica que se lance ElementNotFoundException cuando el email es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(hostRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.generateResetCode(null)
        );

        assertEquals("Huésped no encontrado con email: null", exception.getMessage());
        verify(hostRepository).findByEmail(null);
        verifyNoInteractions(resetCodeServiceImpl);
    }

    /**
     * Prueba el caso edge: cuando el servicio de reset code falla.
     * Verifica que se propague la excepción cuando el servicio de reset code falla.
     */
    @Test
    void shouldHandleEdgeCase_WhenResetCodeServiceFails() throws ElementNotFoundException {
        // Given
        when(hostRepository.findByEmail(validEmail)).thenReturn(Optional.of(host));
        when(resetCodeServiceImpl.generateAndSendCode(host)).thenThrow(new RuntimeException("Email service unavailable"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> hostService.generateResetCode(validEmail)
        );

        assertEquals("Email service unavailable", exception.getMessage());
        verify(hostRepository).findByEmail(validEmail);
        verify(resetCodeServiceImpl).generateAndSendCode(host);
    }
}
