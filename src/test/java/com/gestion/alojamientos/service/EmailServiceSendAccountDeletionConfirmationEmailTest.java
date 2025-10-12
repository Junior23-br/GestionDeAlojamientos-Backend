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
 * Test class for EmailService sendAccountDeletionConfirmationEmail method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceSendAccountDeletionConfirmationEmailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String VALID_EMAIL = "user@email.com";
    private final String INVALID_EMAIL_FORMAT = "invalid-email-format";
    private final String EMPTY_EMAIL = "";
    private final String NULL_EMAIL = null;

    private GuestDto validGuestDto;

    @BeforeEach
    void setUp() {
        // Setup valid GuestDto
        validGuestDto = new GuestDto(
                1L,
                "user@email.com",
                "deleteduser",
                "Carlos Rodríguez",
                "+57 300 555 1234",
                LocalDate.of(1988, 12, 3),
                multipartFile,
                StatesOfGuest.DELETED, // Account marked as deleted
                Role.GUEST,
                List.of(1L, 2L, 3L),
                List.of(1L, 2L),
                List.of(1L, 2L, 3L, 4L)
        );
    }

    /**
     * Test 1: Éxito - Envío exitoso de email de confirmación de eliminación de cuenta
     */
    @Test
    void sendAccountDeletionConfirmationEmail_Success_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendAccountDeletionConfirmationEmail(VALID_EMAIL, validGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 2: Fracaso - Email con formato inválido
     */
    @Test
    void sendAccountDeletionConfirmationEmail_InvalidEmailFormat_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendAccountDeletionConfirmationEmail(INVALID_EMAIL_FORMAT, validGuestDto)
        );

        assertEquals("Email invalido: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 3: Datos inválidos - Email vacío
     */
    @Test
    void sendAccountDeletionConfirmationEmail_EmptyEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendAccountDeletionConfirmationEmail(EMPTY_EMAIL, validGuestDto)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 4: Caso edge - Email nulo
     */
    @Test
    void sendAccountDeletionConfirmationEmail_NullEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendAccountDeletionConfirmationEmail(NULL_EMAIL, validGuestDto)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 5: Caso edge - GuestDto nulo
     */
    @Test
    void sendAccountDeletionConfirmationEmail_NullGuestDto_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendAccountDeletionConfirmationEmail(VALID_EMAIL, null)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de confirmación de eliminación de cuenta"));
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 6: Caso edge - Simular error en el envío del email
     */
    @Test
    void sendAccountDeletionConfirmationEmail_EmailSendingFailure_ShouldThrowInvalidElementException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendAccountDeletionConfirmationEmail(VALID_EMAIL, validGuestDto)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de confirmación de eliminación de cuenta"));
        assertTrue(exception.getMessage().contains("SMTP server error"));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 7: Caso edge - GuestDto con estado ACTIVE (no eliminado)
     */
    @Test
    void sendAccountDeletionConfirmationEmail_ActiveGuestDto_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        GuestDto activeGuestDto = new GuestDto(
                1L,
                "activeuser@email.com",
                "activeuser",
                "Ana Martínez",
                "+57 300 777 8888",
                LocalDate.of(1992, 7, 10),
                multipartFile,
                StatesOfGuest.ACTIVE, // Still active
                Role.GUEST,
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
            emailService.sendAccountDeletionConfirmationEmail(VALID_EMAIL, activeGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 8: Caso edge - GuestDto con rol HOST
     */
    @Test
    void sendAccountDeletionConfirmationEmail_HostGuestDto_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        GuestDto hostGuestDto = new GuestDto(
                1L,
                "host@email.com",
                "deletedhost",
                "Luis Fernández",
                "+57 300 999 0000",
                LocalDate.of(1985, 3, 25),
                multipartFile,
                StatesOfGuest.DELETED,
                Role.HOST, // Host account being deleted
                List.of(1L, 2L),
                List.of(1L, 2L, 3L, 4L, 5L),
                List.of(1L, 2L, 3L)
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendAccountDeletionConfirmationEmail(VALID_EMAIL, hostGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 9: Caso edge - GuestDto con datos mínimos
     */
    @Test
    void sendAccountDeletionConfirmationEmail_MinimalGuestDto_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        GuestDto minimalGuestDto = new GuestDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                StatesOfGuest.DELETED,
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
            emailService.sendAccountDeletionConfirmationEmail(VALID_EMAIL, minimalGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}
