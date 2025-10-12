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
import jakarta.persistence.EntityNotFoundException;
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
 * Clase de prueba para el método cancelBooking del servicio BookingServiceImpl.
 * Prueba la funcionalidad de cancelar una reserva.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceCancelBookingTest {

    @Mock
    private BookingRepo bookingRepo;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private DeleteBookingDTO validDeleteBookingDTO;
    private DeleteBookingDTO invalidPermissionDeleteBookingDTO;
    private DeleteBookingDTO lateCancellationDeleteBookingDTO;
    private DeleteBookingDTO nonExistentBookingDeleteBookingDTO;
    private Guest guest;
    private Host host;
    private Accomodation accommodation;
    private DetailBooking detailBooking;
    private Long validBookingId;
    private Long invalidBookingId;
    private Long validGuestId;
    private Long validHostId;
    private Long invalidGuestId;
    private Long invalidHostId;

    @BeforeEach
    void setUp() {
        validBookingId = 1L;
        invalidBookingId = 99L;
        validGuestId = 1L;
        validHostId = 1L;
        invalidGuestId = 99L;
        invalidHostId = 99L;
        
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

        detailBooking = new DetailBooking();
        detailBooking.setId(1L);
        detailBooking.setCheckInDate(LocalDate.now().plusDays(3)); // 3 days from now
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

        DeleteDetailBookingDTO deleteDetailBookingDTO = new DeleteDetailBookingDTO(
                1L,                             // idDetailBooking
                validBookingId                  // idBooking
        );

        validDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                validGuestId,                   // idGuest
                validHostId                     // idHost
        );

        invalidPermissionDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                invalidGuestId,                 // idGuest (not owner)
                invalidHostId                   // idHost (not owner)
        );

        lateCancellationDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                validGuestId,                   // idGuest
                validHostId                     // idHost
        );

        nonExistentBookingDeleteBookingDTO = new DeleteBookingDTO(
                invalidBookingId,               // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                validGuestId,                   // idGuest
                validHostId                     // idHost
        );
    }

    /**
     * Prueba el caso de éxito: cancelar una reserva con permisos válidos y dentro del plazo.
     * Verifica que se cancele correctamente la reserva y se retorne true.
     */
    @Test
    void shouldReturnTrue_WhenCancellationIsSuccessful() throws Exception {
        // Given
        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);

        // When
        boolean result = bookingService.cancelBooking(validDeleteBookingDTO);

        // Then
        assertTrue(result);
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo).save(booking);
        assertEquals(StatesOfBooking.CANCELLED, booking.getBookingState());
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
                () -> bookingService.cancelBooking(nonExistentBookingDeleteBookingDTO)
        );

        assertEquals("Reserva no encontrada", exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(invalidBookingId);
        verify(bookingRepo, never()).save(any());
    }

    /**
     * Prueba el caso de datos inválidos: sin permisos para cancelar.
     * Verifica que se lance Exception cuando el usuario no tiene permisos para cancelar.
     */
    @Test
    void shouldHandleInvalidData_WhenUserHasNoPermission() {
        // Given
        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.cancelBooking(invalidPermissionDeleteBookingDTO)
        );

        assertEquals("No tienes permisos para cancelar esta reserva", exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo, never()).save(any());
    }

    /**
     * Prueba el caso edge: cancelación fuera del plazo de 48 horas.
     * Verifica que se lance Exception cuando se intenta cancelar muy cerca del check-in.
     */
    @Test
    void shouldHandleEdgeCase_WhenCancellationIsTooLate() {
        // Given
        detailBooking.setCheckInDate(LocalDate.now().plusDays(1)); // Check-in tomorrow
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
}
