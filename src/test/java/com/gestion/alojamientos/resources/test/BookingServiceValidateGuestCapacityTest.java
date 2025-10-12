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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método validateGuestCapacity del servicio BookingServiceImpl.
 * Prueba la funcionalidad de validación de capacidad de huéspedes.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceValidateGuestCapacityTest {

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

    private DetailBookingCreateDTO validCapacityDetailBookingCreateDTO;
    private DetailBookingCreateDTO exceedCapacityDetailBookingCreateDTO;
    private DetailBookingCreateDTO zeroGuestsDetailBookingCreateDTO;
    private DetailBookingCreateDTO negativeGuestsDetailBookingCreateDTO;
    private Accomodation accommodation;
    private Guest guest;
    private Long validAccommodationId;
    private Long validGuestId;

    @BeforeEach
    void setUp() {
        validAccommodationId = 1L;
        validGuestId = 1L;
        
        validCapacityDetailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate
                2,                              // numberOfGuest (within capacity)
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        exceedCapacityDetailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate
                5,                              // numberOfGuest (exceeds capacity)
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        zeroGuestsDetailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate
                0,                              // numberOfGuest (zero)
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        negativeGuestsDetailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate
                -1,                             // numberOfGuest (negative)
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
        accommodation.setMaxGuestCapacity(4);   // Maximum capacity: 4 guests
        accommodation.setApprovalStatus(ApprovalStatus.APPROVED);
        accommodation.setOperationalStatus(OperationalStatus.ACTIVE);

        guest = new Guest();
        guest.setId(validGuestId);
        guest.setEmail("guest@test.com");
        guest.setName("Guest Name");
    }

    /**
     * Prueba el caso de éxito: número de huéspedes dentro de la capacidad.
     * Verifica que no se lance excepción cuando el número de huéspedes es válido.
     */
    @Test
    void shouldNotThrowException_WhenGuestCapacityIsValid() throws Exception {
        // Given
        BookingCreateDTO validCapacityBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                validCapacityDetailBookingCreateDTO, // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any())).thenReturn(null);
        when(bookingRepo.save(any())).thenReturn(null);

        // When & Then
        assertDoesNotThrow(() -> bookingService.createBooking(validCapacityBookingCreateDTO));
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
    }

    /**
     * Prueba el caso de fracaso: número de huéspedes excede la capacidad.
     * Verifica que se lance Exception cuando el número de huéspedes supera la capacidad máxima.
     */
    @Test
    void shouldThrowException_WhenGuestCapacityIsExceeded() {
        // Given
        BookingCreateDTO exceedCapacityBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                exceedCapacityDetailBookingCreateDTO, // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.createBooking(exceedCapacityBookingCreateDTO)
        );

        assertEquals("La capacidad máxima es de 4 huéspedes", exception.getMessage());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
    }

    /**
     * Prueba el caso de datos inválidos: número de huéspedes es cero.
     * Verifica que se lance Exception cuando el número de huéspedes es cero.
     */
    @Test
    void shouldHandleInvalidData_WhenNumberOfGuestsIsZero() {
        // Given
        BookingCreateDTO zeroGuestsBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                zeroGuestsDetailBookingCreateDTO, // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.createBooking(zeroGuestsBookingCreateDTO)
        );

        assertEquals("El número de huéspedes debe ser mayor a 0", exception.getMessage());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
    }

    /**
     * Prueba el caso edge: número de huéspedes negativo.
     * Verifica que se lance Exception cuando el número de huéspedes es negativo.
     */
    @Test
    void shouldHandleEdgeCase_WhenNumberOfGuestsIsNegative() {
        // Given
        BookingCreateDTO negativeGuestsBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                negativeGuestsDetailBookingCreateDTO, // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.createBooking(negativeGuestsBookingCreateDTO)
        );

        assertEquals("El número de huéspedes debe ser mayor a 0", exception.getMessage());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
    }
}
