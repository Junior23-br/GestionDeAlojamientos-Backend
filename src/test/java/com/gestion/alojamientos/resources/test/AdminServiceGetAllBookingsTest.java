package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.accomodation.Services;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getAllBookings del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener todas las reservas.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetAllBookingsTest {

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Booking booking1;
    private Booking booking2;
    private BookingDTO bookingDTO1;
    private BookingDTO bookingDTO2;
    private List<Booking> bookings;
    private List<BookingDTO> bookingDTOs;
    private List<Services> servicesList = new ArrayList<>();


    @BeforeEach
    void setUp() {
        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBookingState(StatesOfBooking.CONFIRMED);
        booking1.setTotalPrice(150.0);
        booking1.setPaymentStatus(true);

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBookingState(StatesOfBooking.CONFIRMED);
        booking2.setTotalPrice(200.0);
        booking2.setPaymentStatus(true);



        DetailBookingDTO detailBookingDTO1 = new DetailBookingDTO(
                1L, null, null, 2, 75.0, 0.0, null, null, servicesList, 1L
        );

        DetailBookingDTO detailBookingDTO2 = new DetailBookingDTO(
                2L, null, null, 3, 100.0, 0.0, null, null, servicesList, 2L
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
     * Prueba el caso de éxito: obtener todas las reservas.
     * Verifica que se retorne correctamente la lista de BookingDTO.
     */
    @Test
    void shouldReturnAllBookings_WhenBookingsExist() {
        // Given
        when(bookingRepo.findAll()).thenReturn(bookings);
        when(bookingMapper.toDto(booking1)).thenReturn(bookingDTO1);
        when(bookingMapper.toDto(booking2)).thenReturn(bookingDTO2);

        // When
        List<BookingDTO> result = adminService.getAllBookings();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookingDTO1, result.get(0));
        assertEquals(bookingDTO2, result.get(1));
        verify(bookingRepo).findAll();
        verify(bookingMapper).toDto(booking1);
        verify(bookingMapper).toDto(booking2);
    }

    /**
     * Prueba el caso de fracaso: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando bookingRepo o bookingMapper son null.
     */
    @Test
    void shouldThrowUnsupportedOperationException_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → bookingRepo y bookingMapper = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.getAllBookings()
        );

        assertEquals("BookingRepo o BookingMapper no están disponibles (GET ALL BOOKINGS).", exception.getMessage());
    }

    /**
     * Prueba el caso de datos inválidos: cuando el repositorio retorna null.
     * Verifica que se maneje correctamente cuando el repositorio retorna null.
     */
    @Test
    void shouldHandleInvalidData_WhenRepositoryReturnsNull() {
        // Given
        when(bookingRepo.findAll()).thenReturn(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> adminService.getAllBookings());
        verify(bookingRepo).findAll();
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso edge: cuando no hay reservas en el sistema.
     * Verifica que se retorne una lista vacía cuando no existen reservas.
     */
    @Test
    void shouldHandleEdgeCase_WhenNoBookingsExist() {
        // Given
        when(bookingRepo.findAll()).thenReturn(Collections.emptyList());

        // When
        List<BookingDTO> result = adminService.getAllBookings();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepo).findAll();
        verifyNoInteractions(bookingMapper);
    }
}
