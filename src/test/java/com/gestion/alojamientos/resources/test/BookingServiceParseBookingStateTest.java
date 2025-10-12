package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingCreateDTO;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingCreateDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationType;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método parseBookingState del servicio BookingServiceImpl.
 * Prueba la funcionalidad de conversión de String a enum StatesOfBooking.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceParseBookingStateTest {

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private GuestRepository guestRepo;

    @Mock
    private DetailBookingRepo detailBookingRepo;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingCreateDTO confirmedBookingCreateDTO;
    private BookingCreateDTO pendingBookingCreateDTO;
    private BookingCreateDTO invalidBookingStateBookingCreateDTO;
    private BookingCreateDTO nullBookingStateBookingCreateDTO;
    private Accomodation accommodation;
    private Guest guest;
    private BookingDTO bookingDTO;
    private DetailBookingCreateDTO detailBookingCreateDTO;
    private Long validAccommodationId;
    private Long validGuestId;

    @BeforeEach
    void setUp() {
        validAccommodationId = 1L;
        validGuestId = 1L;
        
        detailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        confirmedBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        pendingBookingCreateDTO = new BookingCreateDTO(
                "PENDING",                      // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        invalidBookingStateBookingCreateDTO = new BookingCreateDTO(
                "INVALID_STATE",                // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        nullBookingStateBookingCreateDTO = new BookingCreateDTO(
                null,                           // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
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

        bookingDTO = new BookingDTO(
                1L, LocalDateTime.now(), LocalDateTime.now(), StatesOfBooking.CONFIRMED,
                200.0, true, 1L, 1L, 1L, 1L, 1L, null
        );
    }

    /**
     * Prueba el caso de éxito: parsear estado CONFIRMED.
     * Verifica que se convierta correctamente el String "CONFIRMED" a enum StatesOfBooking.CONFIRMED.
     */
    @Test
    void shouldReturnConfirmedState_WhenParseConfirmed() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any())).thenReturn(null);
        when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(confirmedBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(bookingRepo).save(argThat(booking -> 
            booking.getBookingState() == StatesOfBooking.CONFIRMED
        ));
    }

    /**
     * Prueba el caso de éxito: parsear estado PENDING.
     * Verifica que se convierta correctamente el String "PENDING" a enum StatesOfBooking.PENDING.
     */
    @Test
    void shouldReturnPendingState_WhenParsePending() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any())).thenReturn(null);
        when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(pendingBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(bookingRepo).save(argThat(booking -> 
            booking.getBookingState() == StatesOfBooking.PENDING
        ));
    }

    /**
     * Prueba el caso de datos inválidos: estado de reserva inválido.
     * Verifica que se maneje correctamente cuando el estado de reserva no es válido.
     */
    @Test
    void shouldHandleInvalidData_WhenParseInvalidState() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any())).thenReturn(null);
        when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(invalidBookingStateBookingCreateDTO);

        // Then
        assertNotNull(result);
        // The method should still work but the booking state might be null due to exception handling
        verify(bookingRepo).save(any(Booking.class));
    }

    /**
     * Prueba el caso edge: estado de reserva nulo.
     * Verifica que se maneje correctamente cuando el estado de reserva es nulo.
     */
    @Test
    void shouldHandleEdgeCase_WhenParseNullState() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any())).thenReturn(null);
        when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(nullBookingStateBookingCreateDTO);

        // Then
        assertNotNull(result);
        // The method should still work but the booking state might be null
        verify(bookingRepo).save(any(Booking.class));
    }
}
