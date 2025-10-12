package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.repository.user.GuestRepository;
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
 * Clase de prueba para el método getGuestBookings del servicio BookingServiceImpl.
 * Prueba la funcionalidad de obtener las reservas de un huésped específico.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceGetGuestBookingsTest {

    @Mock
    private GuestRepository guestRepo;

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private List<Booking> bookings;
    private List<BookingDTO> bookingDTOs;
    private Long validGuestId;
    private Long invalidGuestId;
    private Long nonExistentGuestId;

    @BeforeEach
    void setUp() {
        validGuestId = 1L;
        invalidGuestId = -1L;
        nonExistentGuestId = 99L;
        
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBookingState(StatesOfBooking.CONFIRMED);
        booking1.setTotalPrice(200.0);
        booking1.setPaymentStatus(true);
        booking1.setCreationDate(LocalDateTime.now());
        booking1.setUpdateTime(LocalDateTime.now());

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBookingState(StatesOfBooking.CHECK_OUT);
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
                2L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1), StatesOfBooking.CHECK_OUT,
                300.0, true, 1L, 1L, 1L, 1L, 1L, detailBookingDTO2
        );

        bookingDTOs = Arrays.asList(bookingDTO1, bookingDTO2);
    }

    /**
     * Prueba el caso de éxito: obtener reservas de un huésped válido.
     * Verifica que se retornen correctamente las reservas del huésped.
     */
    @Test
    void shouldReturnBookingList_WhenGuestExists() throws Exception {
        // Given
        when(guestRepo.existsById(validGuestId)).thenReturn(true);
        when(bookingRepo.findByGuestIdWithDetails(validGuestId)).thenReturn(bookings);
        when(bookingMapper.toDto(bookings.get(0))).thenReturn(bookingDTOs.get(0));
        when(bookingMapper.toDto(bookings.get(1))).thenReturn(bookingDTOs.get(1));

        // When
        List<BookingDTO> result = bookingService.getGuestBookings(validGuestId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
        assertEquals(StatesOfBooking.CONFIRMED, result.get(0).bookingState());
        assertEquals(StatesOfBooking.CHECK_OUT, result.get(1).bookingState());
        verify(guestRepo).existsById(validGuestId);
        verify(bookingRepo).findByGuestIdWithDetails(validGuestId);
        verify(bookingMapper, times(2)).toDto(any(Booking.class));
    }

    /**
     * Prueba el caso de fracaso: cuando el huésped no existe.
     * Verifica que se lance EntityNotFoundException cuando el huésped no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenGuestDoesNotExist() {
        // Given
        when(guestRepo.existsById(nonExistentGuestId)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getGuestBookings(nonExistentGuestId)
        );

        assertEquals("Huésped no encontrado", exception.getMessage());
        verify(guestRepo).existsById(nonExistentGuestId);
        verifyNoInteractions(bookingRepo);
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso de datos inválidos: ID de huésped inválido.
     * Verifica que se lance EntityNotFoundException cuando el ID es inválido.
     */
    @Test
    void shouldHandleInvalidData_WhenGuestIdIsInvalid() {
        // Given
        when(guestRepo.existsById(invalidGuestId)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getGuestBookings(invalidGuestId)
        );

        assertEquals("Huésped no encontrado", exception.getMessage());
        verify(guestRepo).existsById(invalidGuestId);
        verifyNoInteractions(bookingRepo);
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso edge: huésped sin reservas.
     * Verifica que se retorne una lista vacía cuando el huésped no tiene reservas.
     */
    @Test
    void shouldHandleEdgeCase_WhenGuestHasNoBookings() throws Exception {
        // Given
        when(guestRepo.existsById(validGuestId)).thenReturn(true);
        when(bookingRepo.findByGuestIdWithDetails(validGuestId)).thenReturn(Collections.emptyList());

        // When
        List<BookingDTO> result = bookingService.getGuestBookings(validGuestId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(guestRepo).existsById(validGuestId);
        verify(bookingRepo).findByGuestIdWithDetails(validGuestId);
        verifyNoInteractions(bookingMapper);
    }
}
