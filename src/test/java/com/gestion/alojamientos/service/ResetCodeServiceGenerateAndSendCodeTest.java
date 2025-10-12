package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.ResetCodeServiceImpl;

@ExtendWith(MockitoExtension.class)
class ResetCodeServiceGenerateAndSendCodeTest {

    @Mock
    private EmailService emailService;

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private ResetCodeServiceImpl resetCodeService;

    private Guest testGuest;

    @BeforeEach
    void setUp() {
        // Setup test guest
        testGuest = new Guest();
        testGuest.setId(1L);
        testGuest.setEmail("test@example.com");
        testGuest.setUsername("testuser");
        testGuest.setPassword("password123");
    }

    // Éxito: Generación y envío exitoso de código
    @Test
    void generateAndSendCode_Success() throws InvalidElementException {
        // Arrange
        when(guestRepository.save(any(Guest.class))).thenReturn(testGuest);

        // Act
        String result = resetCodeService.generateAndSendCode(testGuest);

        // Assert
        assertNotNull(result);
        assertEquals(7, result.length());
        assertTrue(result.matches("[A-Z0-9]+"));

        verify(emailService).SendResetCodeEmail("test@example.com", result);
        verify(guestRepository).save(testGuest);

        // Verify reset code was set
        assertNotNull(testGuest.getResetCode());
        assertEquals(result, testGuest.getResetCode().getResetCode());
        assertNotNull(testGuest.getResetCode().getExpirationDate());
    }

    // Fracaso: Error en el envío de email
    @Test
    void generateAndSendCode_Failure_EmailServiceError() throws InvalidElementException {
        // Arrange
        doThrow(new InvalidElementException("Error enviando email"))
                .when(emailService).SendResetCodeEmail(anyString(), anyString());

        // Act & Assert
        InvalidElementException exception = assertThrows(InvalidElementException.class,
                () -> resetCodeService.generateAndSendCode(testGuest));

        assertEquals("Error enviando email", exception.getMessage());
        verify(emailService).SendResetCodeEmail(anyString(), anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    // Datos inválidos: Usuario con email nulo
    @Test
    void generateAndSendCode_InvalidData_NullEmail() throws InvalidElementException {
        // Arrange
        testGuest.setEmail(null);

        // Act & Assert
        assertThrows(Exception.class,
                () -> resetCodeService.generateAndSendCode(testGuest));

        verify(emailService, never()).SendResetCodeEmail(anyString(), anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    // Edge case: Usuario con email vacío
    @Test
    void generateAndSendCode_EdgeCase_EmptyEmail() throws InvalidElementException {
        // Arrange
        testGuest.setEmail("");

        // Act & Assert
        assertThrows(Exception.class,
                () -> resetCodeService.generateAndSendCode(testGuest));

        verify(emailService, never()).SendResetCodeEmail(anyString(), anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }
}
