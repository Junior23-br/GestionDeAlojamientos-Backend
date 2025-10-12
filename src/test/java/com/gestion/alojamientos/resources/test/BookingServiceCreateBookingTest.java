package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingCreateDTO;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingCreateDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationType;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.booking.DetailBooking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.repository.booking.DetailBookingRepo;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método createBooking del servicio BookingServiceImpl.
 * Prueba la funcionalidad de crear una nueva reserva.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceCreateBookingTest {

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

    private BookingCreateDTO validBookingCreateDTO;
    private BookingCreateDTO invalidAccommodationBookingCreateDTO;
    private BookingCreateDTO unavailableAccommodationBookingCreateDTO;
    private BookingCreateDTO invalidGuestBookingCreateDTO;
    private Accomodation accommodation;
    private Guest guest;
    private DetailBooking detailBooking;
    private Booking booking;
    private BookingDTO bookingDTO;
    private DetailBookingCreateDTO detailBookingCreateDTO;
    private Long validAccommodationId;
    private Long invalidAccommodationId;
    private Long validGuestId;
    private Long invalidGuestId;

    @BeforeEach
    void setUp() {
        validAccommodationId = 1L;
        invalidAccommodationId = 99L;
        validGuestId = 1L;
        invalidGuestId = 99L;
        
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

        validBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        invalidAccommodationBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                invalidAccommodationId,         // idAccommodation
                validGuestId                    // guestId
        );

        unavailableAccommodationBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        invalidGuestBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                invalidGuestId                  // guestId
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

        detailBooking = new DetailBooking();
        detailBooking.setId(1L);
        detailBooking.setCheckInDate(detailBookingCreateDTO.checkInDate());
        detailBooking.setCheckOutDate(detailBookingCreateDTO.checkOutDate());
        detailBooking.setNumberOfGuest(detailBookingCreateDTO.numberOfGuest());
        detailBooking.setPriceNightAccommodation(detailBookingCreateDTO.priceNightAccommodation());
        detailBooking.setSubTotal(detailBookingCreateDTO.subTotal());
        detailBooking.setDiscount(detailBookingCreateDTO.discount());

        booking = new Booking();
        booking.setId(1L);
        booking.setBookingState(StatesOfBooking.CONFIRMED);
        booking.setTotalPrice(200.0);
        booking.setPaymentStatus(true);
        booking.setCreationDate(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        booking.setAccomodation(accommodation);
        booking.setGuest(guest);
        booking.setDetailBooking(detailBooking);

        DetailBookingDTO detailBookingDTO = new DetailBookingDTO(
                1L, detailBookingCreateDTO.checkInDate(), detailBookingCreateDTO.checkOutDate(),
                2, 100.0, 200.0, 0.0, null, List.of(), 1L
        );

        bookingDTO = new BookingDTO(
                1L, LocalDateTime.now(), LocalDateTime.now(), StatesOfBooking.CONFIRMED,
                200.0, true, 1L, 1L, 1L, 1L, 1L, detailBookingDTO
        );
    }

    /**
     * Prueba el caso de éxito: crear una reserva con datos válidos.
     * Verifica que se cree correctamente la reserva y se retorne el BookingDTO.
     */
    @Test
    void shouldReturnBookingDTO_WhenBookingIsCreatedSuccessfully() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(validAccommodationId, detailBookingCreateDTO.checkInDate(), detailBookingCreateDTO.checkOutDate())).thenReturn(false);
        when(detailBookingRepo.save(any(DetailBooking.class))).thenReturn(detailBooking);
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(validBookingCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(StatesOfBooking.CONFIRMED, result.bookingState());
        assertEquals(200.0, result.totalPrice());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(validGuestId);
        verify(bookingRepo).isAccommodationBooked(validAccommodationId, detailBookingCreateDTO.checkInDate(), detailBookingCreateDTO.checkOutDate());
        verify(detailBookingRepo).save(any(DetailBooking.class));
        verify(bookingRepo).save(any(Booking.class));
        verify(bookingMapper).toDto(booking);
    }

    /**
     * Prueba el caso de fracaso: cuando el alojamiento no existe.
     * Verifica que se lance EntityNotFoundException cuando el alojamiento no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenAccommodationDoesNotExist() {
        // Given
        when(accommodationRepo.findById(invalidAccommodationId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.createBooking(invalidAccommodationBookingCreateDTO)
        );

        assertEquals("Alojamiento no encontrado", exception.getMessage());
        verify(accommodationRepo).findById(invalidAccommodationId);
        verifyNoInteractions(guestRepo);
        verifyNoInteractions(bookingRepo);
    }

    /**
     * Prueba el caso de datos inválidos: cuando el alojamiento no está disponible.
     * Verifica que se lance Exception cuando el alojamiento no está operativo o aprobado.
     */
    @Test
    void shouldHandleInvalidData_WhenAccommodationIsNotAvailable() {
        // Given
        accommodation.setOperationalStatus(OperationalStatus.INACTIVE);
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> bookingService.createBooking(unavailableAccommodationBookingCreateDTO)
        );

        assertEquals("El alojamiento no está disponible para reservas", exception.getMessage());
        verify(accommodationRepo).findById(validAccommodationId);
        verifyNoInteractions(guestRepo);
        verifyNoInteractions(bookingRepo);
    }

    /**
     * Prueba el caso edge: cuando el huésped no existe.
     * Verifica que se lance EntityNotFoundException cuando el huésped no existe.
     */
    @Test
    void shouldHandleEdgeCase_WhenGuestDoesNotExist() {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(invalidGuestId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.createBooking(invalidGuestBookingCreateDTO)
        );

        assertEquals("Huésped no encontrado", exception.getMessage());
        verify(accommodationRepo).findById(validAccommodationId);
        verify(guestRepo).findById(invalidGuestId);
        verifyNoInteractions(bookingRepo);
    }
}
