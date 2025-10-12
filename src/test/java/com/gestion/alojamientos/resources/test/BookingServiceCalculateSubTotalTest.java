package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingCreateDTO;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingCreateDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationType;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.booking.DetailBooking;
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
 * Clase de prueba para el método calculateSubTotal del servicio BookingServiceImpl.
 * Prueba la funcionalidad de cálculo del subtotal de la reserva.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceCalculateSubTotalTest {

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

    private BookingCreateDTO normalCalculationBookingCreateDTO;
    private BookingCreateDTO withDiscountBookingCreateDTO;
    private BookingCreateDTO highDiscountBookingCreateDTO;
    private BookingCreateDTO singleNightBookingCreateDTO;
    private Accomodation accommodation;
    private Guest guest;
    private DetailBooking detailBooking;
    private BookingDTO bookingDTO;
    private DetailBookingCreateDTO detailBookingCreateDTO;
    private Long validAccommodationId;
    private Long validGuestId;

    @BeforeEach
    void setUp() {
        validAccommodationId = 1L;
        validGuestId = 1L;
        
        // Normal calculation: 2 nights * 100.0 = 200.0
        detailBookingCreateDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate (2 nights)
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        normalCalculationBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingCreateDTO,         // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        // With discount: 2 nights * 100.0 - 50.0 = 150.0
        DetailBookingCreateDTO withDiscountDetailDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate (2 nights)
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                150.0,                          // subTotal
                50.0,                           // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        withDiscountBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                150.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                withDiscountDetailDTO,          // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        // High discount: 2 nights * 100.0 - 250.0 = -50.0 (should return 0.0)
        DetailBookingCreateDTO highDiscountDetailDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate (2 nights)
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                0.0,                            // subTotal (should be 0.0)
                250.0,                          // discount (higher than total)
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        highDiscountBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                0.0,                            // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                highDiscountDetailDTO,          // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        // Single night: 1 night * 100.0 = 100.0
        DetailBookingCreateDTO singleNightDetailDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(2),    // checkOutDate (1 night)
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                100.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        singleNightBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                100.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                singleNightDetailDTO,           // detailBookingCreateDTO
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

        detailBooking = new DetailBooking();
        detailBooking.setId(1L);
        detailBooking.setCheckInDate(detailBookingCreateDTO.checkInDate());
        detailBooking.setCheckOutDate(detailBookingCreateDTO.checkOutDate());
        detailBooking.setNumberOfGuest(detailBookingCreateDTO.numberOfGuest());
        detailBooking.setPriceNightAccommodation(detailBookingCreateDTO.priceNightAccommodation());
        detailBooking.setSubTotal(detailBookingCreateDTO.subTotal());
        detailBooking.setDiscount(detailBookingCreateDTO.discount());

        bookingDTO = new BookingDTO(
                1L, LocalDateTime.now(), LocalDateTime.now(), StatesOfBooking.CONFIRMED,
                200.0, true, 1L, 1L, 1L, 1L, 1L, null
        );
    }

    /**
     * Prueba el caso de éxito: cálculo normal del subtotal.
     * Verifica que se calcule correctamente el subtotal sin descuento.
     */
    @Test
    void shouldReturnCorrectSubTotal_WhenCalculationIsNormal() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any(DetailBooking.class))).thenAnswer(invocation -> {
            DetailBooking detailBooking = invocation.getArgument(0);
            detailBooking.setId(1L);
            return detailBooking;
        });
        when(bookingRepo.save(any())).thenReturn(null);
        when(bookingMapper.toDto(any())).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(normalCalculationBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(detailBookingRepo).save(argThat(detailBooking -> 
            detailBooking.getSubTotal().equals(200.0) // 2 nights * 100.0 = 200.0
        ));
    }

    /**
     * Prueba el caso de éxito: cálculo con descuento.
     * Verifica que se calcule correctamente el subtotal con descuento aplicado.
     */
    @Test
    void shouldReturnCorrectSubTotal_WhenCalculationWithDiscount() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any(DetailBooking.class))).thenAnswer(invocation -> {
            DetailBooking detailBooking = invocation.getArgument(0);
            detailBooking.setId(1L);
            return detailBooking;
        });
        when(bookingRepo.save(any())).thenReturn(null);
        when(bookingMapper.toDto(any())).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(withDiscountBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(detailBookingRepo).save(argThat(detailBooking -> 
            detailBooking.getSubTotal().equals(150.0) // 2 nights * 100.0 - 50.0 = 150.0
        ));
    }

    /**
     * Prueba el caso de datos inválidos: descuento mayor al total.
     * Verifica que se retorne 0.0 cuando el descuento es mayor al subtotal.
     */
    @Test
    void shouldHandleInvalidData_WhenDiscountExceedsTotal() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any(DetailBooking.class))).thenAnswer(invocation -> {
            DetailBooking detailBooking = invocation.getArgument(0);
            detailBooking.setId(1L);
            return detailBooking;
        });
        when(bookingRepo.save(any())).thenReturn(null);
        when(bookingMapper.toDto(any())).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(highDiscountBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(detailBookingRepo).save(argThat(detailBooking -> 
            detailBooking.getSubTotal().equals(0.0) // Math.max(-50.0, 0.0) = 0.0
        ));
    }

    /**
     * Prueba el caso edge: estadía de una sola noche.
     * Verifica que se calcule correctamente el subtotal para una sola noche.
     */
    @Test
    void shouldHandleEdgeCase_WhenSingleNightStay() throws Exception {
        // Given
        when(accommodationRepo.findById(validAccommodationId)).thenReturn(Optional.of(accommodation));
        when(guestRepo.findById(validGuestId)).thenReturn(Optional.of(guest));
        when(bookingRepo.isAccommodationBooked(any(), any(), any())).thenReturn(false);
        when(detailBookingRepo.save(any(DetailBooking.class))).thenAnswer(invocation -> {
            DetailBooking detailBooking = invocation.getArgument(0);
            detailBooking.setId(1L);
            return detailBooking;
        });
        when(bookingRepo.save(any())).thenReturn(null);
        when(bookingMapper.toDto(any())).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(singleNightBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(detailBookingRepo).save(argThat(detailBooking -> 
            detailBooking.getSubTotal().equals(100.0) // 1 night * 100.0 = 100.0
        ));
    }
}
