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
 * Test class for EmailService sendRoleChangeConfirmationEmail method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceSendRoleChangeConfirmationEmailTest {

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
        // Setup valid GuestDto (user who changed from GUEST to HOST)
        validGuestDto = new GuestDto(
                1L,
                "user@email.com",
                "newhost",
                "Pedro Sánchez",
                "+57 300 111 2222",
                LocalDate.of(1990, 4, 18),
                multipartFile,
                StatesOfGuest.ACTIVE,
                Role.HOST, // Changed to HOST role
                List.of(1L, 2L, 3L),
                List.of(1L, 2L),
                List.of(1L, 2L, 3L, 4L)
        );
    }

    /**
     * Test 1: Éxito - Envío exitoso de email de confirmación de cambio de rol
     */
    @Test
    void sendRoleChangeConfirmationEmail_Success_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendRoleChangeConfirmationEmail(VALID_EMAIL, validGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 2: Fracaso - Email con formato inválido
     */
    @Test
    void sendRoleChangeConfirmationEmail_InvalidEmailFormat_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendRoleChangeConfirmationEmail(INVALID_EMAIL_FORMAT, validGuestDto)
        );

        assertEquals("Email invalido: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 3: Datos inválidos - Email vacío
     */
    @Test
    void sendRoleChangeConfirmationEmail_EmptyEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendRoleChangeConfirmationEmail(EMPTY_EMAIL, validGuestDto)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 4: Caso edge - Email nulo
     */
    @Test
    void sendRoleChangeConfirmationEmail_NullEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendRoleChangeConfirmationEmail(NULL_EMAIL, validGuestDto)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 5: Caso edge - GuestDto nulo
     */
    @Test
    void sendRoleChangeConfirmationEmail_NullGuestDto_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendRoleChangeConfirmationEmail(VALID_EMAIL, null)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de confirmación de cambio de rol"));
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 6: Caso edge - Simular error en el envío del email
     */
    @Test
    void sendRoleChangeConfirmationEmail_EmailSendingFailure_ShouldThrowInvalidElementException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendRoleChangeConfirmationEmail(VALID_EMAIL, validGuestDto)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de confirmación de cambio de rol"));
        assertTrue(exception.getMessage().contains("SMTP server error"));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 7: Caso edge - GuestDto con rol GUEST (no cambió de rol)
     */
    @Test
    void sendRoleChangeConfirmationEmail_GuestRole_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        GuestDto guestRoleDto = new GuestDto(
                1L,
                "guest@email.com",
                "guestuser",
                "María López",
                "+57 300 333 4444",
                LocalDate.of(1995, 9, 12),
                multipartFile,
                StatesOfGuest.ACTIVE,
                Role.GUEST, // Still GUEST role
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
            emailService.sendRoleChangeConfirmationEmail(VALID_EMAIL, guestRoleDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 8: Caso edge - GuestDto con estado SUSPENDED
     */
    @Test
    void sendRoleChangeConfirmationEmail_SuspendedGuestDto_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        GuestDto suspendedGuestDto = new GuestDto(
                1L,
                "suspended@email.com",
                "suspendeduser",
                "Roberto Silva",
                "+57 300 555 6666",
                LocalDate.of(1987, 11, 8),
                multipartFile,
                StatesOfGuest.SUSPENDED, // Suspended account
                Role.HOST,
                List.of(1L, 2L),
                List.of(1L),
                List.of(1L, 2L, 3L)
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendRoleChangeConfirmationEmail(VALID_EMAIL, suspendedGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 9: Caso edge - GuestDto con datos mínimos
     */
    @Test
    void sendRoleChangeConfirmationEmail_MinimalGuestDto_ShouldSendEmailWithPdf() throws InvalidElementException {
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
                Role.HOST,
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
            emailService.sendRoleChangeConfirmationEmail(VALID_EMAIL, minimalGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 10: Caso edge - GuestDto con múltiples métodos de pago y reservas
     */
    @Test
    void sendRoleChangeConfirmationEmail_GuestWithMultipleData_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        GuestDto complexGuestDto = new GuestDto(
                1L,
                "complex@email.com",
                "complexuser",
                "Elena Vargas",
                "+57 300 777 8888",
                LocalDate.of(1993, 6, 30),
                multipartFile,
                StatesOfGuest.ACTIVE,
                Role.HOST,
                List.of(1L, 2L, 3L, 4L, 5L), // Multiple payment methods
                List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L), // Multiple bookings
                List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L) // Multiple transactions
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendRoleChangeConfirmationEmail(VALID_EMAIL, complexGuestDto);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}
