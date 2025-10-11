package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

/**
 * Test class for EmailService SendNewBookingEmail method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceSendNewBookingEmailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private BookingDTO validBookingDTO;
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

        // Setup valid BookingDTO
        validBookingDTO = new BookingDTO(
                1L,
                LocalDateTime.now(),
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
     * Test 1: Éxito - Envío exitoso de email de nueva reserva
     */
    @Test
    void sendNewBookingEmail_Success_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendNewBookingEmail(VALID_EMAIL, validBookingDTO, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 2: Fracaso - Email con formato inválido
     */
    @Test
    void sendNewBookingEmail_InvalidEmailFormat_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendNewBookingEmail(INVALID_EMAIL_FORMAT, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("Email invalido: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 3: Datos inválidos - Email vacío
     */
    @Test
    void sendNewBookingEmail_EmptyEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendNewBookingEmail(EMPTY_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 4: Caso edge - Email nulo
     */
    @Test
    void sendNewBookingEmail_NullEmail_ShouldThrowInvalidElementException() {
        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendNewBookingEmail(NULL_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertEquals("El email no puede estar vacio", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /**
     * Test 5: Caso edge - BookingDTO nulo
     */
    @Test
    void sendNewBookingEmail_NullBookingDTO_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendNewBookingEmail(VALID_EMAIL, null, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 6: Caso edge - DetailBookingDTO nulo
     */
    @Test
    void sendNewBookingEmail_NullDetailBookingDTO_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendNewBookingEmail(VALID_EMAIL, validBookingDTO, null);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 7: Caso edge - Reserva con estado CANCELED
     */
    @Test
    void sendNewBookingEmail_CanceledBooking_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        BookingDTO canceledBooking = new BookingDTO(
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
                validDetailBookingDTO
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendNewBookingEmail(VALID_EMAIL, canceledBooking, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 8: Caso edge - Reserva con estado PENDING
     */
    @Test
    void sendNewBookingEmail_PendingBooking_ShouldSendEmailWithPdf() throws InvalidElementException {
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
            emailService.SendNewBookingEmail(VALID_EMAIL, pendingBooking, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 9: Caso edge - Reserva con precio cero
     */
    @Test
    void sendNewBookingEmail_ZeroPriceBooking_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        BookingDTO zeroPriceBooking = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.CONFIRMED,
                0.0,
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
            emailService.SendNewBookingEmail(VALID_EMAIL, zeroPriceBooking, validDetailBookingDTO);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 10: Caso edge - Reserva con fecha de check-in en el pasado
     */
    @Test
    void sendNewBookingEmail_PastCheckInDate_ShouldSendEmailWithPdf() throws InvalidElementException {
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
                StatesOfBooking.CONFIRMED,
                250.0,
                true,
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
            emailService.SendNewBookingEmail(VALID_EMAIL, pastCheckInBooking, pastCheckInDetail);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 11: Caso edge - Reserva con muchos servicios
     */
    @Test
    void sendNewBookingEmail_BookingWithManyServices_ShouldSendEmailWithPdf() throws InvalidElementException {
        // Arrange
        // Create mock Services objects
        Services wifiService = new Services();
        wifiService.setId(1L);
        wifiService.setName("WIFI");
        
        Services parkingService = new Services();
        parkingService.setId(2L);
        parkingService.setName("PARKING");
        
        Services breakfastService = new Services();
        breakfastService.setId(3L);
        breakfastService.setName("BREAKFAST");
        
        List<Services> manyServices = List.of(
                wifiService,
                parkingService,
                breakfastService
        );

        DetailBookingDTO servicesDetail = new DetailBookingDTO(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                100.0,
                200.0,
                10.0,
                validDetailBookingDTO.serviceFeeDTO(),
                manyServices,
                1L
        );

        BookingDTO servicesBooking = new BookingDTO(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatesOfBooking.CONFIRMED,
                300.0,
                true,
                1L,
                1L,
                1L,
                1L,
                1L,
                servicesDetail
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doAnswer(invocation -> {
            // Simulate successful email sending
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> {
            emailService.SendNewBookingEmail(VALID_EMAIL, servicesBooking, servicesDetail);
        });

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Test 12: Caso edge - Simular error en el envío del email
     */
    @Test
    void sendNewBookingEmail_EmailSendingFailure_ShouldThrowInvalidElementException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP server error")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> emailService.SendNewBookingEmail(VALID_EMAIL, validBookingDTO, validDetailBookingDTO)
        );

        assertTrue(exception.getMessage().contains("Error al enviar el email de nueva reserva"));
        assertTrue(exception.getMessage().contains("SMTP server error"));
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}
