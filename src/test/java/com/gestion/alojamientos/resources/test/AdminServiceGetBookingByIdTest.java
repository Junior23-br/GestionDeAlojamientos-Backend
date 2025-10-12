package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getBookingById del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener una reserva por su ID.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetBookingByIdTest {

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Booking booking;
    private BookingDTO bookingDTO;
    private Long validBookingId;
    private Long invalidBookingId;

    @BeforeEach
    void setUp() {
        validBookingId = 1L;
        invalidBookingId = 99L;
        
        booking = new Booking();
        booking.setId(validBookingId);
        booking.setBookingState(StatesOfBooking.CONFIRMED);
        booking.setTotalPrice(150.0);
        booking.setPaymentStatus(true);

        DetailBookingDTO detailBookingDTO = new DetailBookingDTO(
                1L, null, null, 2, 75.0, 0.0, null, null, 1L
        );

        bookingDTO = new BookingDTO(
                validBookingId, LocalDateTime.now(), LocalDateTime.now(), StatesOfBooking.CONFIRMED,
                150.0, true, 1L, 1L, 1L, 1L, 1L, detailBookingDTO
        );
    }

    /**
     * Prueba el caso de éxito: obtener una reserva existente por ID.
     * Verifica que se retorne correctamente el BookingDTO cuando la reserva existe.
     */
    @Test
    void shouldReturnBookingDTO_WhenBookingExists() {
        // Given
        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDTO);

        // When
        BookingDTO result = adminService.getBookingById(validBookingId);

        // Then
        assertNotNull(result);
        assertEquals(validBookingId, result.id());
        assertEquals(StatesOfBooking.CONFIRMED, result.bookingState());
        assertEquals(150.0, result.totalPrice());
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingMapper).toDto(booking);
    }

    /**
     * Prueba el caso de fracaso: cuando la reserva no existe.
     * Verifica que se lance EntityNotFoundException cuando el ID no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenBookingDoesNotExist() {
        // Given
        when(bookingRepo.findByIdWithAllDetails(invalidBookingId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getBookingById(invalidBookingId)
        );

        assertEquals("Reserva no encontrada con ID: " + invalidBookingId, exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(invalidBookingId);
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se lance EntityNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(bookingRepo.findByIdWithAllDetails(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getBookingById(null)
        );

        assertEquals("Reserva no encontrada con ID: null", exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(null);
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso edge: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando bookingRepo o bookingMapper son null.
     */
    @Test
    void shouldHandleEdgeCase_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → bookingRepo y bookingMapper = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.getBookingById(validBookingId)
        );

        assertEquals("BookingRepo o BookingMapper no están disponibles (GET BOOKING BY ID).", exception.getMessage());
    }
}
