package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

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
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;

import jakarta.mail.internet.MimeMessage;

/**
 * Test class for EmailService sendCancelledBookingEmail method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceSendCancelledBookingEmailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private BookingDTO validCancelledBookingDTO;
    private DetailBookingDTO validDetailBookingDTO;
    private final String VALID_EMAIL = "juan.perez@email.com";
    private final String INVALID_EMAIL_FORMAT = "invalid-email-format";
    private final String EMPTY_EMAIL = "";
    private final String NULL_EMAIL = null;

    @BeforeEach
    void setUp() {
        // Setup valid ServiceFeeDTO
        ServiceFeeDTO serviceFeeDTO = new ServiceFeeDTO(
                1L,
                "Service fee description",
                50.0,
                "FIXED",
                4.0,
                1
        );

        // Setup valid DetailBookingDTO
        validDetailBookingDTO = new DetailBookingDTO(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                100.0,
                200.0,
                10.0,
                serviceFeeDTO,
                Collections.emptyList(),
                1L
        );

        // Setup valid cancelled BookingDTO
        validCancelledBookingDTO = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.CANCELLED,
                250.0,
                false, // Payment status false for cancelled booking
                1L,
                1L,
                1L,
                1L,
                1L,
                validDetailBookingDTO
        );
    }

    /**
     * Test 1: Éxito - Envío exitoso de email de reserva cancelada
     */
    @Test
    void sendCancelledBookingEmail_Success_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCancelledBookingEmail(VALID_EMAIL, validCancelledBookingDTO, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 2: Fracaso - Email con formato inválido
     */
    @Test
    void sendCancelledBookingEmail_InvalidEmailFormat_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCancelledBookingEmail(INVALID_EMAIL_FORMAT, validCancelledBookingDTO, validDetailBookingDTO)
        );

        assertEquals("Email invalido: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 3: Datos inválidos - Email vacío
     */
    @Test
    void sendCancelledBookingEmail_EmptyEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCancelledBookingEmail(EMPTY_EMAIL, validCancelledBookingDTO, validDetailBookingDTO)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 4: Caso edge - Email nulo
     */
    @Test
    void sendCancelledBookingEmail_NullEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCancelledBookingEmail(NULL_EMAIL, validCancelledBookingDTO, validDetailBookingDTO)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 5: Caso edge - BookingDTO nulo
     */
    @Test
    void sendCancelledBookingEmail_NullBookingDTO_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCancelledBookingEmail(VALID_EMAIL, null, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 6: Caso edge - DetailBookingDTO nulo
     */
    @Test
    void sendCancelledBookingEmail_NullDetailBookingDTO_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCancelledBookingEmail(VALID_EMAIL, validCancelledBookingDTO, null);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 7: Caso edge - Reserva cancelada con estado CONFIRMED (contradictorio pero debería funcionar)
     */
    @Test
    void sendCancelledBookingEmail_ConfirmedBooking_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        BookingDTO confirmedBooking = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.CONFIRMED, // Contradictory state but should still work
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
            emailService.sendCancelledBookingEmail(VALID_EMAIL, confirmedBooking, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 8: Caso edge - Reserva cancelada con estado PENDING
     */
    @Test
    void sendCancelledBookingEmail_PendingBooking_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        BookingDTO pendingBooking = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.PENDING,
                250.0,
                false,
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
            emailService.sendCancelledBookingEmail(VALID_EMAIL, pendingBooking, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 9: Caso edge - Reserva cancelada con precio cero
     */
    @Test
    void sendCancelledBookingEmail_ZeroPriceBooking_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        BookingDTO zeroPriceBooking = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.CANCELLED,
                0.0,
                false,
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
            emailService.sendCancelledBookingEmail(VALID_EMAIL, zeroPriceBooking, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 10: Caso edge - Reserva cancelada con fecha de check-in en el pasado
     */
    @Test
    void sendCancelledBookingEmail_PastCheckInDate_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        DetailBookingDTO pastCheckInDetail = new DetailBookingDTO(
                1L,
                LocalDate.now().minusDays(1), // Past check-in date
                LocalDate.now().plusDays(1),
                2,
                100.0,
                200.0,
                10.0,
                validDetailBookingDTO.serviceFeeDTO(),
                Collections.emptyList(),
                1L
        );

        BookingDTO pastCheckInBooking = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.CANCELLED,
                250.0,
                false,
                1L,
                1L,
                1L,
                1L,
                1L,
                pastCheckInDetail
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCancelledBookingEmail(VALID_EMAIL, pastCheckInBooking, pastCheckInDetail);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 11: Caso edge - Reserva cancelada con descuento alto
     */
    @Test
    void sendCancelledBookingEmail_HighDiscountBooking_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        DetailBookingDTO highDiscountDetail = new DetailBookingDTO(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                100.0,
                200.0,
                50.0, // High discount
                validDetailBookingDTO.serviceFeeDTO(),
                Collections.emptyList(),
                1L
        );

        BookingDTO highDiscountBooking = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.CANCELLED,
                200.0, // Adjusted total price
                false,
                1L,
                1L,
                1L,
                1L,
                1L,
                highDiscountDetail
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCancelledBookingEmail(VALID_EMAIL, highDiscountBooking, highDiscountDetail);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 12: Caso edge - Reserva cancelada con muchos huéspedes
     */
    @Test
    void sendCancelledBookingEmail_ManyGuestsBooking_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        DetailBookingDTO manyGuestsDetail = new DetailBookingDTO(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                10, // Many guests
                100.0,
                200.0,
                10.0,
                validDetailBookingDTO.serviceFeeDTO(),
                Collections.emptyList(),
                1L
        );

        BookingDTO manyGuestsBooking = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.CANCELLED,
                500.0, // Higher total price for many guests
                false,
                1L,
                1L,
                1L,
                1L,
                1L,
                manyGuestsDetail
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.sendCancelledBookingEmail(VALID_EMAIL, manyGuestsBooking, manyGuestsDetail);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 13: Caso edge - Simular error en el envío del email
     */
    @Test
    void sendCancelledBookingEmail_EmailSendingFailure_ShouldThrowInvalidElementException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.sendCancelledBookingEmail(VALID_EMAIL, validCancelledBookingDTO, validDetailBookingDTO)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de cancelación"));
        assertTrue(exception.getMessage().contains("SMTP server error"));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}
