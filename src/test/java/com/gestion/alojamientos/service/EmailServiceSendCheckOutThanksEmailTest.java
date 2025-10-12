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
 * Test class for EmailService sendCheckOutThanksEmail method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceSendCheckOutThanksEmailTest {

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

        // Setup valid DetailBookingDTO (completed stay)
        validDetailBookingDTO = new DetailBookingDTO(
                1L,
                LocalDate.now().minusDays(3), // Check-in 3 days ago
                LocalDate.now().minusDays(1), // Check-out yesterday
                2,
                100.0,
                200.0,
                20.0, // Discount applied
                validServiceFeeDTO,
                List.of(
                    createService(1L, "WiFi"),
                    createService(2L, "Breakfast")
                ),
                1L
        );

        // Setup valid BookingDTO (completed booking)
        validBookingDTO = new BookingDTO(
                1L,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(1),
                StatesOfBooking.CHECK_OUT,
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
     * Test 1: Éxito - Envío exitoso de email de agradecimiento por check-out
     */
    @Test
    void sendCheckOutThanksEmail_Success_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCheckOutThanksEmail(VALID_EMAIL, validBookingDTO, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 2: Fracaso - Email con formato inválido
     */
    @Test
    void sendCheckOutThanksEmail_InvalidEmailFormat_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckOutThanksEmail(INVALID_EMAIL_FORMAT, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("Email invalido: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 3: Datos inválidos - Email vacío
     */
    @Test
    void sendCheckOutThanksEmail_EmptyEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckOutThanksEmail(EMPTY_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 4: Caso edge - Email nulo
     */
    @Test
    void sendCheckOutThanksEmail_NullEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckOutThanksEmail(NULL_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 5: Caso edge - BookingDTO nulo
     */
    @Test
    void sendCheckOutThanksEmail_NullBookingDTO_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckOutThanksEmail(VALID_EMAIL, null, validDetailBookingDTO)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de agradecimiento por check-out"));
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 6: Caso edge - DetailBookingDTO nulo
     */
    @Test
    void sendCheckOutThanksEmail_NullDetailBookingDTO_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckOutThanksEmail(VALID_EMAIL, validBookingDTO, null)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de agradecimiento por check-out"));
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 7: Caso edge - Simular error en el envío del email
     */
    @Test
    void sendCheckOutThanksEmail_EmailSendingFailure_ShouldThrowInvalidElementException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCheckOutThanksEmail(VALID_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de agradecimiento por check-out"));
        assertTrue(exception.getMessage().contains("SMTP server error"));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 8: Caso edge - BookingDTO con estado diferente a COMPLETED
     */
    @Test
    void sendCheckOutThanksEmail_BookingNotCompleted_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        BookingDTO pendingBookingDTO = new BookingDTO(
                1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now(),
                StatesOfBooking.CANCELLED, // Different state
                250.0,
                true,
                1L,
                1L,
                1L,
                1L,
                1L,
                validDetailBookingDTO
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCheckOutThanksEmail(VALID_EMAIL, pendingBookingDTO, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 9: Caso edge - DetailBookingDTO con múltiples servicios
     */
    @Test
    void sendCheckOutThanksEmail_MultipleServices_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        DetailBookingDTO multipleServicesDetail = new DetailBookingDTO(
                1L,
                LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(1),
                4,
                150.0,
                300.0,
                30.0,
                validServiceFeeDTO,
                List.of(
                    createService(1L, "WiFi"),
                    createService(2L, "Breakfast"),
                    createService(3L, "Pool"),
                    createService(4L, "Gym")
                ),
                1L
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCheckOutThanksEmail(VALID_EMAIL, validBookingDTO, multipleServicesDetail);
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
