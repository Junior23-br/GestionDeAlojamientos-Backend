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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;

import jakarta.mail.internet.MimeMessage;

/**
 * Test class for EmailService SendResetCodeEmail method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceSendResetCodeEmailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String VALID_EMAIL = "juan.perez@email.com";
    private final String INVALID_EMAIL_FORMAT = "invalid-email-format";
    private final String EMPTY_EMAIL = "";
    private final String NULL_EMAIL = null;
    private final String VALID_RESET_CODE = "ABC1234";
    private final String LONG_RESET_CODE = "ABCDEFGHIJKLMNOP";
    private final String EMPTY_RESET_CODE = "";
    private final String NULL_RESET_CODE = null;

    @BeforeEach
    void setUp() {
        // Setup is handled by Mockito annotations
    }

    /**
     * Test 1: Éxito - Envío exitoso de email de código de reseteo
     */
    @Test
    void sendResetCodeEmail_Success_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // We need to mock the MimeMessageHelper constructor behavior
        // Since it's a final class, we'll use doAnswer to simulate the behavior
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendResetCodeEmail(VALID_EMAIL, VALID_RESET_CODE);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 2: Fracaso - Email con formato inválido
     */
    @Test
    void sendResetCodeEmail_InvalidEmailFormat_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendResetCodeEmail(INVALID_EMAIL_FORMAT, VALID_RESET_CODE)
        );

        assertEquals("Email invalido: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 3: Datos inválidos - Email vacío
     */
    @Test
    void sendResetCodeEmail_EmptyEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendResetCodeEmail(EMPTY_EMAIL, VALID_RESET_CODE)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 4: Caso edge - Email nulo
     */
    @Test
    void sendResetCodeEmail_NullEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendResetCodeEmail(NULL_EMAIL, VALID_RESET_CODE)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 5: Caso edge - Código de reseteo vacío (debería funcionar)
     */
    @Test
    void sendResetCodeEmail_EmptyResetCode_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendResetCodeEmail(VALID_EMAIL, EMPTY_RESET_CODE);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 6: Caso edge - Código de reseteo nulo (debería funcionar)
     */
    @Test
    void sendResetCodeEmail_NullResetCode_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendResetCodeEmail(VALID_EMAIL, NULL_RESET_CODE);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 7: Caso edge - Código de reseteo muy largo
     */
    @Test
    void sendResetCodeEmail_LongResetCode_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendResetCodeEmail(VALID_EMAIL, LONG_RESET_CODE);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 8: Caso edge - Email con formato válido pero con caracteres especiales
     */
    @Test
    void sendResetCodeEmail_EmailWithSpecialCharacters_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        String emailWithSpecialChars = "test.user+tag@domain.co.uk";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendResetCodeEmail(emailWithSpecialChars, VALID_RESET_CODE);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 9: Caso edge - Email con subdominio
     */
    @Test
    void sendResetCodeEmail_EmailWithSubdomain_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        String emailWithSubdomain = "user@mail.subdomain.example.com";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendResetCodeEmail(emailWithSubdomain, VALID_RESET_CODE);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 10: Caso edge - Email con números en el dominio
     */
    @Test
    void sendResetCodeEmail_EmailWithNumbersInDomain_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        String emailWithNumbers = "user@domain123.com";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendResetCodeEmail(emailWithNumbers, VALID_RESET_CODE);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 11: Caso edge - Email sin dominio (inválido)
     */
    @Test
    void sendResetCodeEmail_EmailWithoutDomain_ShouldThrowInvalidElementException() {
        // Arrange
        String emailWithoutDomain = "user@";

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendResetCodeEmail(emailWithoutDomain, VALID_RESET_CODE)
        );

        assertEquals("Email invalido: " + emailWithoutDomain, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 12: Caso edge - Email sin @ (inválido)
     */
    @Test
    void sendResetCodeEmail_EmailWithoutAtSymbol_ShouldThrowInvalidElementException() {
        // Arrange
        String emailWithoutAt = "userexample.com";

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendResetCodeEmail(emailWithoutAt, VALID_RESET_CODE)
        );

        assertEquals("Email invalido: " + emailWithoutAt, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 13: Caso edge - Email con múltiples @ (inválido)
     */
    @Test
    void sendResetCodeEmail_EmailWithMultipleAtSymbols_ShouldThrowInvalidElementException() {
        // Arrange
        String emailWithMultipleAt = "user@domain@example.com";

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendResetCodeEmail(emailWithMultipleAt, VALID_RESET_CODE)
        );

        assertEquals("Email invalido: " + emailWithMultipleAt, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 14: Caso edge - Simular error en el envío del email
     */
    @Test
    void sendResetCodeEmail_EmailSendingFailure_ShouldThrowInvalidElementException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendResetCodeEmail(VALID_EMAIL, VALID_RESET_CODE)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de recuperación"));
        assertTrue(exception.getMessage().contains("SMTP server error"));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}
