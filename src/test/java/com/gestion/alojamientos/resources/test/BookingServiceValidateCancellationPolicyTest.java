package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.DeleteBookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DeleteDetailBookingDTO;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.booking.DetailBooking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.service.Impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método validateCancellationPolicy del servicio BookingServiceImpl.
 * Prueba la funcionalidad de validación de política de cancelación (48 horas antes del check-in).
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceValidateCancellationPolicyTest {

    @Mock
    private BookingRepo bookingRepo;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private DeleteBookingDTO validCancellationDeleteBookingDTO;
    private DeleteBookingDTO lateCancellationDeleteBookingDTO;
    private DeleteBookingDTO veryLateCancellationDeleteBookingDTO;
    private DeleteBookingDTO sameDayCancellationDeleteBookingDTO;
    private Guest guest;
    private Host host;
    private Accomodation accommodation;
    private DetailBooking detailBooking;
    private Long validBookingId;
    private Long validGuestId;
    private Long validHostId;

    @BeforeEach
    void setUp() {
        validBookingId = 1L;
        validGuestId = 1L;
        validHostId = 1L;
        
        guest = new Guest();
        guest.setId(validGuestId);
        guest.setEmail("guest@test.com");
        guest.setName("Guest Name");

        host = new Host();
        host.setId(validHostId);
        host.setEmail("host@test.com");
        host.setName("Host Name");

        accommodation = new Accomodation();
        accommodation.setId(1L);
        accommodation.setTitle("Casa en la playa");
        accommodation.setHost(host);

        DeleteDetailBookingDTO deleteDetailBookingDTO = new DeleteDetailBookingDTO(
                1L,                             // idDetailBooking
                validBookingId                  // idBooking
        );

        validCancellationDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                validGuestId,                   // idGuest
                validHostId                     // idHost
        );

        lateCancellationDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                validGuestId,                   // idGuest
                validHostId                     // idHost
        );

        veryLateCancellationDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                validGuestId,                   // idGuest
                validHostId                     // idHost
        );

        sameDayCancellationDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                validGuestId,                   // idGuest
                validHostId                     // idHost
        );
    }

    /**
     * Prueba el caso de éxito: cancelación dentro del plazo permitido (más de 48 horas).
     * Verifica que no se lance excepción cuando la cancelación es dentro del plazo.
     */
    @Test
    void shouldNotThrowException_WhenCancellationIsWithinPolicy() throws Exception {
        // Given
        detailBooking = new DetailBooking();
        detailBooking.setId(1L);
        detailBooking.setCheckInDate(LocalDate.now().plusDays(3)); // Check-in in 3 days
        detailBooking.setCheckOutDate(LocalDate.now().plusDays(5));

        booking = new Booking();
        booking.setId(validBookingId);
        booking.setBookingState(StatesOfBooking.CONFIRMED);
        booking.setTotalPrice(200.0);
        booking.setPaymentStatus(true);
        booking.setCreationDate(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        booking.setGuest(guest);
        booking.setAccomodation(accommodation);
        booking.setDetailBooking(detailBooking);

        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);

        // When & Then
        assertDoesNotThrow(() -> bookingService.cancelBooking(validCancellationDeleteBookingDTO));
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo).save(booking);
    }

    /**
     * Prueba el caso de fracaso: cancelación muy tarde (menos de 48 horas).
     * Verifica que se lance Exception cuando la cancelación es muy tarde.
     */
    @Test
    void shouldThrowException_WhenCancellationIsTooLate() {
        // Given
        detailBooking = new DetailBooking();
        detailBooking.setId(1L);
        detailBooking.setCheckInDate(LocalDate.now().plusDays(1)); // Check-in tomorrow
        detailBooking.setCheckOutDate(LocalDate.now().plusDays(3));

        booking = new Booking();
        booking.setId(validBookingId);
        booking.setBookingState(StatesOfBooking.CONFIRMED);
        booking.setTotalPrice(200.0);
        booking.setPaymentStatus(true);
        booking.setCreationDate(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        booking.setGuest(guest);
        booking.setAccomodation(accommodation);
        booking.setDetailBooking(detailBooking);

        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.cancelBooking(lateCancellationDeleteBookingDTO)
        );

        assertEquals("Solo se puede cancelar hasta 48 horas antes del check-in", exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo, never()).save(any());
    }

    /**
     * Prueba el caso de datos inválidos: cancelación el mismo día del check-in.
     * Verifica que se lance Exception cuando se intenta cancelar el mismo día.
     */
    @Test
    void shouldHandleInvalidData_WhenCancellationIsSameDay() {
        // Given
        detailBooking = new DetailBooking();
        detailBooking.setId(1L);
        detailBooking.setCheckInDate(LocalDate.now()); // Check-in today
        detailBooking.setCheckOutDate(LocalDate.now().plusDays(2));

        booking = new Booking();
        booking.setId(validBookingId);
        booking.setBookingState(StatesOfBooking.CONFIRMED);
        booking.setTotalPrice(200.0);
        booking.setPaymentStatus(true);
        booking.setCreationDate(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        booking.setGuest(guest);
        booking.setAccomodation(accommodation);
        booking.setDetailBooking(detailBooking);

        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.cancelBooking(sameDayCancellationDeleteBookingDTO)
        );

        assertEquals("Solo se puede cancelar hasta 48 horas antes del check-in", exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo, never()).save(any());
    }

    /**
     * Prueba el caso edge: cancelación exactamente 48 horas antes.
     * Verifica que se lance Exception cuando la cancelación es exactamente en el límite.
     */
    @Test
    void shouldHandleEdgeCase_WhenCancellationIsExactlyAtDeadline() {
        // Given
        detailBooking = new DetailBooking();
        detailBooking.setId(1L);
        detailBooking.setCheckInDate(LocalDate.now().plusDays(2)); // Check-in in exactly 2 days (48 hours)
        detailBooking.setCheckOutDate(LocalDate.now().plusDays(4));

        booking = new Booking();
        booking.setId(validBookingId);
        booking.setBookingState(StatesOfBooking.CONFIRMED);
        booking.setTotalPrice(200.0);
        booking.setPaymentStatus(true);
        booking.setCreationDate(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        booking.setGuest(guest);
        booking.setAccomodation(accommodation);
        booking.setDetailBooking(detailBooking);

        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.cancelBooking(veryLateCancellationDeleteBookingDTO)
        );

        assertEquals("Solo se puede cancelar hasta 48 horas antes del check-in", exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo, never()).save(any());
    }
}
