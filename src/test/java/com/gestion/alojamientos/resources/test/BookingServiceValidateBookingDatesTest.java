package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingCreateDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingCreateDTO;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationType;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.repository.booking.DetailBookingRepo;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método validateBookingDates del servicio BookingServiceImpl.
 * Prueba la funcionalidad de validación de fechas de reserva.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceValidateBookingDatesTest {

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private GuestRepository guestRepo;

    @Mock
    private DetailBookingRepo detailBookingRepo;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private DetailBookingCreateDTO validDetailBookingCreateDTO;
    private DetailBookingCreateDTO pastDateDetailBookingCreateDTO;
    private DetailBookingCreateDTO sameDateDetailBookingCreateDTO;
    private DetailBookingCreateDTO tooLongStayDetailBookingCreateDTO;
    private Accomodation accommodation;
    private Guest guest;
    private Long validAccommodationId;
    private Long validGuestId;

    @BeforeEach
    void setUp() {
        validAccommodationId = 1L;
        validGuestId = 1L;
        
        validDetailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate (tomorrow)
                LocalDate.now().plusDays(3),    // checkOutDate (3 days from now)
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        pastDateDetailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().minusDays(1),   // checkInDate (yesterday)
                LocalDate.now().plusDays(1),    // checkOutDate
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        sameDateDetailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(1),    // checkOutDate (same as check-in)
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        tooLongStayDetailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(32),   // checkOutDate (31 days later)
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        accommodation = new Accomodation();
        accommodation.setId(validAccommodationId);
        accommodation.setTitle("Casa en la playa");
        accommodation.setAccomodationType(AccomodationType.HOUSE);
        accommodation.setMaxGuestCapacity(4);
        accommodation.setApprovalStatus(ApprovalStatus.APPROVED);
        accommodation.setOperationalStatus(OperationalStatus.ACTIVE);

        guest = new Guest();
        guest.setId(validGuestId);
        guest.setEmail("guest@test.com");
        guest.setName("Guest Name");
    }

    /**
     * Prueba el caso de éxito: fechas válidas para la reserva.
     * Verifica que no se lance excepción cuando las fechas son válidas.
     */
    @Test
    void shouldNotThrowException_WhenDatesAreValid() throws Exception {
        // Given
        BookingCreateDTO validBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                validDetailBookingCreateDTO,    // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any())).thenReturn(null);
        when(bookingRepo.save(any())).thenReturn(null);

        // When & Then
        assertDoesNotThrow(() -> bookingService.createBooking(validBookingCreateDTO));
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
    }

    /**
     * Prueba el caso de fracaso: fecha de check-in en el pasado.
     * Verifica que se lance Exception cuando la fecha de check-in es anterior a hoy.
     */
    @Test
    void shouldThrowException_WhenCheckInDateIsInThePast() {
        // Given
        BookingCreateDTO pastDateBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                pastDateDetailBookingCreateDTO, // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.createBooking(pastDateBookingCreateDTO)
        );

        assertEquals("No se pueden reservar fechas pasadas", exception.getMessage());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
    }

    /**
     * Prueba el caso de datos inválidos: fechas iguales (check-in = check-out).
     * Verifica que se lance Exception cuando las fechas de check-in y check-out son iguales.
     */
    @Test
    void shouldHandleInvalidData_WhenCheckInAndCheckOutDatesAreEqual() {
        // Given
        BookingCreateDTO sameDateBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                sameDateDetailBookingCreateDTO, // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.createBooking(sameDateBookingCreateDTO)
        );

        assertEquals("La estadía mínima es de 1 noche", exception.getMessage());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
    }

    /**
     * Prueba el caso edge: estadía demasiado larga (más de 30 días).
     * Verifica que se lance Exception cuando la estadía supera el máximo permitido.
     */
    @Test
    void shouldHandleEdgeCase_WhenStayIsTooLong() {
        // Given
        BookingCreateDTO tooLongStayBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                tooLongStayDetailBookingCreateDTO, // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.createBooking(tooLongStayBookingCreateDTO)
        );

        assertEquals("La estadía máxima permitida es de 30 días", exception.getMessage());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
    }
}
