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
import org.springframework.web.multipart.MultipartFile;

import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.List;

/**
 * Test class for EmailService sendWelcomeEmail method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceSendWelcomeEmailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String VALID_EMAIL = "newuser@email.com";
    private final String INVALID_EMAIL_FORMAT = "invalid-email-format";
    private final String EMPTY_EMAIL = "";
    private final String NULL_EMAIL = null;

    private GuestDto validGuestDto;

    @BeforeEach
    void setUp() {
        // Setup valid GuestDto
        validGuestDto = new GuestDto(
                1L,
                "newuser@email.com",
                "newuser",
                "Juan Pérez",
                "+57 300 123 4567",
                LocalDate.of(1990, 5, 15),
                multipartFile,
                StatesOfGuest.ACTIVE,
                Role.GUEST,
                List.of(1L, 2L),
                List.of(1L),
                List.of(1L, 2L, 3L)
        );
    }

    /**
     * Test 1: Éxito - Envío exitoso de email de bienvenida
     */
    @Test
    void sendWelcomeEmail_Success_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendWelcomeEmail(VALID_EMAIL, validGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 2: Fracaso - Email con formato inválido
     */
    @Test
    void sendWelcomeEmail_InvalidEmailFormat_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendWelcomeEmail(INVALID_EMAIL_FORMAT, validGuestDto)
        );

        assertEquals("Email invalido: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 3: Datos inválidos - Email vacío
     */
    @Test
    void sendWelcomeEmail_EmptyEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendWelcomeEmail(EMPTY_EMAIL, validGuestDto)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 4: Caso edge - Email nulo
     */
    @Test
    void sendWelcomeEmail_NullEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendWelcomeEmail(NULL_EMAIL, validGuestDto)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 5: Caso edge - GuestDto nulo
     */
    @Test
    void sendWelcomeEmail_NullGuestDto_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendWelcomeEmail(VALID_EMAIL, null)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de bienvenida"));
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 6: Caso edge - Simular error en el envío del email
     */
    @Test
    void sendWelcomeEmail_EmailSendingFailure_ShouldThrowInvalidElementException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendWelcomeEmail(VALID_EMAIL, validGuestDto)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de bienvenida"));
        assertTrue(exception.getMessage().contains("SMTP server error"));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 7: Caso edge - GuestDto con datos mínimos
     */
    @Test
    void sendWelcomeEmail_MinimalGuestDto_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        GuestDto minimalGuestDto = new GuestDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                StatesOfGuest.ACTIVE,
                Role.GUEST,
                List.of(),
                List.of(),
                List.of()
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendWelcomeEmail(VALID_EMAIL, minimalGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 8: Caso edge - GuestDto con rol HOST
     */
    @Test
    void sendWelcomeEmail_GuestDtoWithHostRole_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        GuestDto hostGuestDto = new GuestDto(
                1L,
                "host@email.com",
                "hostuser",
                "María García",
                "+57 300 987 6543",
                LocalDate.of(1985, 8, 20),
                multipartFile,
                StatesOfGuest.ACTIVE,
                Role.HOST,
                List.of(1L),
                List.of(1L, 2L, 3L),
                List.of(1L, 2L)
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendWelcomeEmail(VALID_EMAIL, hostGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}
