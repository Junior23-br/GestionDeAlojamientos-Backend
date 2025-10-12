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

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.dto.transaction.ServiceFee.ServiceFeeDTO;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.accomodation.Services;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Test class for EmailService sendCheckInReminderEmail method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceSendCheckInReminderEmailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String VALID_EMAIL = "guest@email.com";
    private final String INVALID_EMAIL_FORMAT = "invalid-email-format";
    private final String EMPTY_EMAIL = "";
    private final String NULL_EMAIL = null;

    private BookingDTO validBookingDTO;
    private DetailBookingDTO validDetailBookingDTO;
    private ServiceFeeDTO validServiceFeeDTO;

    @BeforeEach
    void setUp() {
        // Setup valid ServiceFeeDTO
        validServiceFeeDTO = new ServiceFeeDTO(1L, "Service fee", 50.0, "percentage", 0.1, 5);

        // Setup valid DetailBookingDTO
        validDetailBookingDTO = new DetailBookingDTO(
                1L,
                LocalDate.now().plusDays(1), // Check-in tomorrow
                LocalDate.now().plusDays(3), // Check-out in 3 days
                2,
                100.0,
                200.0,
                0.0,
                validServiceFeeDTO,
                List.of(createService(1L, "WiFi")),
                1L
        );

        // Setup valid BookingDTO
        validBookingDTO = new BookingDTO(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                StatesOfBooking.CONFIRMED,
                250.0,
                true,
                1L,
                1L,
                1L,
                1L,
                1L,
                validDetailBookingDTO
        );
    }

    /**
     * Test 1: Éxito - Envío exitoso de email de recordatorio de check-in
     */
    @Test
    void sendCheckInReminderEmail_Success_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCheckInReminderEmail(VALID_EMAIL, validBookingDTO, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 2: Fracaso - Email con formato inválido
     */
    @Test
    void sendCheckInReminderEmail_InvalidEmailFormat_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckInReminderEmail(INVALID_EMAIL_FORMAT, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("Email invalido: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 3: Datos inválidos - Email vacío
     */
    @Test
    void sendCheckInReminderEmail_EmptyEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckInReminderEmail(EMPTY_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 4: Caso edge - Email nulo
     */
    @Test
    void sendCheckInReminderEmail_NullEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckInReminderEmail(NULL_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 5: Caso edge - BookingDTO nulo
     */
    @Test
    void sendCheckInReminderEmail_NullBookingDTO_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckInReminderEmail(VALID_EMAIL, null, validDetailBookingDTO)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de recordatorio de check-in"));
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 6: Caso edge - DetailBookingDTO nulo
     */
    @Test
    void sendCheckInReminderEmail_NullDetailBookingDTO_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckInReminderEmail(VALID_EMAIL, validBookingDTO, null)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de recordatorio de check-in"));
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 7: Caso edge - Simular error en el envío del email
     */
    @Test
    void sendCheckInReminderEmail_EmailSendingFailure_ShouldThrowInvalidElementException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckInReminderEmail(VALID_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de recordatorio de check-in"));
        assertTrue(exception.getMessage().contains("SMTP server error"));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 8: Caso edge - BookingDTO con datos mínimos
     */
    @Test
    void sendCheckInReminderEmail_MinimalBookingDTO_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        BookingDTO minimalBookingDTO = new BookingDTO(
                null,
                null,
                null,
                StatesOfBooking.PENDING,
                0.0,
                false,
                null,
                null,
                null,
                null,
                null,
                validDetailBookingDTO
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCheckInReminderEmail(VALID_EMAIL, minimalBookingDTO, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    private Services createService(Long id, String name) {
        Services service = new Services();
        service.setId(id);
        service.setName(name);
        return service;
    }
}
