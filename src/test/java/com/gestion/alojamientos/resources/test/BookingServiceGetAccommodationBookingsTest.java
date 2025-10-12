package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.service.Impl.BookingServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getAccommodationBookings del servicio BookingServiceImpl.
 * Prueba la funcionalidad de obtener las reservas de un alojamiento específico.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceGetAccommodationBookingsTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private List<Booking> bookings;
    private List<BookingDTO> bookingDTOs;
    private Long validAccommodationId;
    private Long invalidAccommodationId;
    private Long nonExistentAccommodationId;

    @BeforeEach
    void setUp() {
        validAccommodationId = 1L;
        invalidAccommodationId = -1L;
        nonExistentAccommodationId = 99L;
        
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBookingState(StatesOfBooking.CONFIRMED);
        booking1.setTotalPrice(200.0);
        booking1.setPaymentStatus(true);
        booking1.setCreationDate(LocalDateTime.now());
        booking1.setUpdateTime(LocalDateTime.now());

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBookingState(StatesOfBooking.COMPLETED);
        booking2.setTotalPrice(300.0);
        booking2.setPaymentStatus(true);
        booking2.setCreationDate(LocalDateTime.now().minusDays(1));
        booking2.setUpdateTime(LocalDateTime.now().minusDays(1));

        bookings = Arrays.asList(booking1, booking2);

        DetailBookingDTO detailBookingDTO1 = new DetailBookingDTO(
                1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3),
                2, 100.0, 200.0, 0.0, null, null, 1L
        );

        DetailBookingDTO detailBookingDTO2 = new DetailBookingDTO(
                2L, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1),
                2, 150.0, 300.0, 0.0, null, null, 2L
        );

        BookingDTO bookingDTO1 = new BookingDTO(
                1L, LocalDateTime.now(), LocalDateTime.now(), StatesOfBooking.CONFIRMED,
                200.0, true, 1L, 1L, 1L, 1L, 1L, detailBookingDTO1
        );

        BookingDTO bookingDTO2 = new BookingDTO(
                2L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1), StatesOfBooking.COMPLETED,
                300.0, true, 1L, 1L, 1L, 1L, 1L, detailBookingDTO2
        );

        bookingDTOs = Arrays.asList(bookingDTO1, bookingDTO2);
    }

    /**
     * Prueba el caso de éxito: obtener reservas de un alojamiento válido.
     * Verifica que se retornen correctamente las reservas del alojamiento.
     */
    @Test
    void shouldReturnBookingList_WhenAccommodationExists() throws Exception {
        // Given
        when(accommodationRepo.existsById(validAccommodationId)).thenReturn(true);
        when(bookingRepo.findByAccommodationId(validAccommodationId)).thenReturn(bookings);
        when(bookingMapper.toDto(bookings.get(0))).thenReturn(bookingDTOs.get(0));
        when(bookingMapper.toDto(bookings.get(1))).thenReturn(bookingDTOs.get(1));

        // When
        List<BookingDTO> result = bookingService.getAccommodationBookings(validAccommodationId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
        assertEquals(StatesOfBooking.CONFIRMED, result.get(0).bookingState());
        assertEquals(StatesOfBooking.COMPLETED, result.get(1).bookingState());
        verify(accommodationRepo).existsById(validAccommodationId);
        verify(bookingRepo).findByAccommodationId(validAccommodationId);
        verify(bookingMapper, times(2)).toDto(any(Booking.class));
    }

    /**
     * Prueba el caso de fracaso: cuando el alojamiento no existe.
     * Verifica que se lance EntityNotFoundException cuando el alojamiento no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenAccommodationDoesNotExist() {
        // Given
        when(accommodationRepo.existsById(nonExistentAccommodationId)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getAccommodationBookings(nonExistentAccommodationId)
        );

        assertEquals("Alojamiento no encontrado", exception.getMessage());
        verify(accommodationRepo).existsById(nonExistentAccommodationId);
        verifyNoInteractions(bookingRepo);
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso de datos inválidos: ID de alojamiento inválido.
     * Verifica que se lance EntityNotFoundException cuando el ID es inválido.
     */
    @Test
    void shouldHandleInvalidData_WhenAccommodationIdIsInvalid() {
        // Given
        when(accommodationRepo.existsById(invalidAccommodationId)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getAccommodationBookings(invalidAccommodationId)
        );

        assertEquals("Alojamiento no encontrado", exception.getMessage());
        verify(accommodationRepo).existsById(invalidAccommodationId);
        verifyNoInteractions(bookingRepo);
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso edge: alojamiento sin reservas.
     * Verifica que se retorne una lista vacía cuando el alojamiento no tiene reservas.
     */
    @Test
    void shouldHandleEdgeCase_WhenAccommodationHasNoBookings() throws Exception {
        // Given
        when(accommodationRepo.existsById(validAccommodationId)).thenReturn(true);
        when(bookingRepo.findByAccommodationId(validAccommodationId)).thenReturn(Collections.emptyList());

        // When
        List<BookingDTO> result = bookingService.getAccommodationBookings(validAccommodationId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accommodationRepo).existsById(validAccommodationId);
        verify(bookingRepo).findByAccommodationId(validAccommodationId);
        verifyNoInteractions(bookingMapper);
    }
}
