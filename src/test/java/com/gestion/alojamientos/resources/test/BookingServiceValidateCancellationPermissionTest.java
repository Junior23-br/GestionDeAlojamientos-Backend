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
 * Clase de prueba para el método validateCancellationPermission del servicio BookingServiceImpl.
 * Prueba la funcionalidad de validación de permisos para cancelar una reserva.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceValidateCancellationPermissionTest {

    @Mock
    private BookingRepo bookingRepo;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private DeleteBookingDTO guestOwnerDeleteBookingDTO;
    private DeleteBookingDTO hostOwnerDeleteBookingDTO;
    private DeleteBookingDTO noPermissionDeleteBookingDTO;
    private DeleteBookingDTO nullIdsDeleteBookingDTO;
    private Guest guest;
    private Host host;
    private Accomodation accommodation;
    private DetailBooking detailBooking;
    private Long validBookingId;
    private Long validGuestId;
    private Long validHostId;
    private Long invalidGuestId;
    private Long invalidHostId;

    @BeforeEach
    void setUp() {
        validBookingId = 1L;
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
        detailBooking.setCheckInDate(LocalDate.now().plusDays(3));
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

        guestOwnerDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                validGuestId,                   // idGuest (owner)
                invalidHostId                   // idHost (not owner)
        );

        hostOwnerDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                invalidGuestId,                 // idGuest (not owner)
                validHostId                     // idHost (owner)
        );

        noPermissionDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                invalidGuestId,                 // idGuest (not owner)
                invalidHostId                   // idHost (not owner)
        );

        nullIdsDeleteBookingDTO = new DeleteBookingDTO(
                validBookingId,                 // idBooking
                1L,                             // idAccommodation
                deleteDetailBookingDTO,         // deleteDetailBookingDTO
                null,                           // idGuest (null)
                null                            // idHost (null)
        );
    }

    /**
     * Prueba el caso de éxito: huésped propietario de la reserva.
     * Verifica que no se lance excepción cuando el huésped es propietario de la reserva.
     */
    @Test
    void shouldNotThrowException_WhenGuestIsOwner() throws Exception {
        // Given
        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);

        // When & Then
        assertDoesNotThrow(() -> bookingService.cancelBooking(guestOwnerDeleteBookingDTO));
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo).save(booking);
    }

    /**
     * Prueba el caso de éxito: anfitrión propietario del alojamiento.
     * Verifica que no se lance excepción cuando el anfitrión es propietario del alojamiento.
     */
    @Test
    void shouldNotThrowException_WhenHostIsOwner() throws Exception {
        // Given
        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);

        // When & Then
        assertDoesNotThrow(() -> bookingService.cancelBooking(hostOwnerDeleteBookingDTO));
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo).save(booking);
    }

    /**
     * Prueba el caso de datos inválidos: usuario sin permisos.
     * Verifica que se lance Exception cuando el usuario no tiene permisos para cancelar.
     */
    @Test
    void shouldHandleInvalidData_WhenUserHasNoPermission() {
        // Given
        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.cancelBooking(noPermissionDeleteBookingDTO)
        );

        assertEquals("No tienes permisos para cancelar esta reserva", exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo, never()).save(any());
    }

    /**
     * Prueba el caso edge: IDs nulos en el DTO.
     * Verifica que se lance Exception cuando los IDs son nulos.
     */
    @Test
    void shouldHandleEdgeCase_WhenIdsAreNull() {
        // Given
        when(bookingRepo.findByIdWithAllDetails(validBookingId)).thenReturn(Optional.of(booking));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.cancelBooking(nullIdsDeleteBookingDTO)
        );

        assertEquals("No tienes permisos para cancelar esta reserva", exception.getMessage());
        verify(bookingRepo).findByIdWithAllDetails(validBookingId);
        verify(bookingRepo, never()).save(any());
    }
}
