package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getHostBookingsHistory del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener el historial de reservas de un host.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetHostBookingsHistoryTest {

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private HostRepo hostRepo;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Long validHostId;
    private Long invalidHostId;
    private Booking booking1;
    private Booking booking2;
    private BookingDTO bookingDTO1;
    private BookingDTO bookingDTO2;
    private List<Booking> bookings;
    private List<BookingDTO> bookingDTOs;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        invalidHostId = 99L;
        
        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBookingState(StatesOfBooking.CONFIRMED);
        booking1.setTotalPrice(150.0);
        booking1.setPaymentStatus(true);

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBookingState(StatesOfBooking.CHECK_OUT);
        booking2.setTotalPrice(200.0);
        booking2.setPaymentStatus(true);

        DetailBookingDTO detailBookingDTO1 = new DetailBookingDTO(
                1L, null, null, 2, 75.0, 0.0, null, null, null, 1L
        );

        DetailBookingDTO detailBookingDTO2 = new DetailBookingDTO(
                2L, null, null, 3, 100.0, 0.0, null, null, null, 2L
        );

        bookingDTO1 = new BookingDTO(
                1L, LocalDateTime.now(), LocalDateTime.now(), StatesOfBooking.CONFIRMED,
                150.0, true, 1L, 1L, 1L, 1L, 1L, detailBookingDTO1
        );

        bookingDTO2 = new BookingDTO(
                2L, LocalDateTime.now(), LocalDateTime.now(), StatesOfBooking.CHECK_OUT,
                200.0, true, 1L, 2L, 1L, 1L, 1L, detailBookingDTO2
        );

        bookings = Arrays.asList(booking1, booking2);
        bookingDTOs = Arrays.asList(bookingDTO1, bookingDTO2);
    }

    /**
     * Prueba el caso de éxito: obtener el historial de reservas de un host existente.
     * Verifica que se retorne correctamente la lista de BookingDTO.
     */
    @Test
    void shouldReturnBookingHistory_WhenHostExists() {
        // Given
        when(hostRepo.existsById(validHostId)).thenReturn(true);
        when(bookingRepo.findByGuestIdWithDetails(validHostId)).thenReturn(bookings);
        when(bookingMapper.toDto(booking1)).thenReturn(bookingDTO1);
        when(bookingMapper.toDto(booking2)).thenReturn(bookingDTO2);

        // When
        List<BookingDTO> result = adminService.getHostBookingsHistory(validHostId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookingDTO1, result.get(0));
        assertEquals(bookingDTO2, result.get(1));
        verify(hostRepo).existsById(validHostId);
        verify(bookingRepo).findByGuestIdWithDetails(validHostId);
        verify(bookingMapper).toDto(booking1);
        verify(bookingMapper).toDto(booking2);
    }

    /**
     * Prueba el caso de fracaso: cuando el host no existe.
     * Verifica que se lance EntityNotFoundException cuando el host no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenHostDoesNotExist() {
        // Given
        when(hostRepo.existsById(invalidHostId)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getHostBookingsHistory(invalidHostId)
        );

        assertEquals("Host no encontrado con ID: " + invalidHostId, exception.getMessage());
        verify(hostRepo).existsById(invalidHostId);
        verify(bookingRepo, never()).findByGuestIdWithDetails(any());
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se lance EntityNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(hostRepo.existsById(null)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getHostBookingsHistory(null)
        );

        assertEquals("Host no encontrado con ID: null", exception.getMessage());
        verify(hostRepo).existsById(null);
        verify(bookingRepo, never()).findByGuestIdWithDetails(any());
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
                () -> service.getHostBookingsHistory(validHostId)
        );

        assertEquals("BookingRepo o BookingMapper no están disponibles (HOST BOOKINGS).", exception.getMessage());
    }
}
