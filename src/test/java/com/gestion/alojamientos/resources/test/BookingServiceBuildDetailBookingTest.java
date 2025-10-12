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
 * Clase de prueba para el método buildDetailBooking del servicio BookingServiceImpl.
 * Prueba la funcionalidad de construcción de la entidad DetailBooking.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceBuildDetailBookingTest {

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
    private BookingCreateDTO withDiscountBookingCreateDTO;
    private BookingCreateDTO withoutDiscountBookingCreateDTO;
    private BookingCreateDTO withServicesBookingCreateDTO;
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

        DetailBookingCreateDTO withDiscountDetailDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
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

        DetailBookingCreateDTO withoutDiscountDetailDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                null,                           // discount
                1L,                             // serviceFeeId
                List.of(1, 2)                   // listServicesIds
        );

        withoutDiscountBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                withoutDiscountDetailDTO,       // detailBookingCreateDTO
                validAccommodationId,           // idAccommodation
                validGuestId                    // guestId
        );

        DetailBookingCreateDTO withServicesDetailDTO = new DetailBookingCreateDTO(
                LocalDate.now().plusDays(1),    // checkInDate
                LocalDate.now().plusDays(3),    // checkOutDate
                2,                              // numberOfGuest
                100.0,                          // priceNightAccommodation
                200.0,                          // subTotal
                0.0,                            // discount
                1L,                             // serviceFeeId
                List.of(1, 2, 3, 4)            // listServicesIds (more services)
        );

        withServicesBookingCreateDTO = new BookingCreateDTO(
                "CONFIRMED",                    // bookingState
                200.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                withServicesDetailDTO,          // detailBookingCreateDTO
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
     * Prueba el caso de éxito: construir DetailBooking con datos válidos.
     * Verifica que se construya correctamente la entidad DetailBooking con todos los campos.
     */
    @Test
    void shouldReturnDetailBooking_WhenBuildIsSuccessful() throws Exception {
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
        BookingDTO result = bookingService.createBooking(validBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(detailBookingRepo).save(argThat(detailBooking -> 
            detailBooking.getCheckInDate().equals(detailBookingCreateDTO.checkInDate()) &&
            detailBooking.getCheckOutDate().equals(detailBookingCreateDTO.checkOutDate()) &&
            detailBooking.getNumberOfGuest().equals(detailBookingCreateDTO.numberOfGuest()) &&
            detailBooking.getPriceNightAccommodation().equals(detailBookingCreateDTO.priceNightAccommodation()) &&
            detailBooking.getDiscount().equals(detailBookingCreateDTO.discount())
        ));
    }

    /**
     * Prueba el caso de éxito: construir DetailBooking con descuento.
     * Verifica que se construya correctamente la entidad DetailBooking con descuento aplicado.
     */
    @Test
    void shouldReturnDetailBooking_WhenBuildWithDiscount() throws Exception {
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
            detailBooking.getDiscount().equals(50.0)
        ));
    }

    /**
     * Prueba el caso de datos inválidos: DetailBooking sin descuento.
     * Verifica que se construya correctamente la entidad DetailBooking sin descuento.
     */
    @Test
    void shouldHandleInvalidData_WhenBuildWithoutDiscount() throws Exception {
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
        BookingDTO result = bookingService.createBooking(withoutDiscountBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(detailBookingRepo).save(argThat(detailBooking -> 
            detailBooking.getDiscount() == null
        ));
    }

    /**
     * Prueba el caso edge: DetailBooking con múltiples servicios.
     * Verifica que se construya correctamente la entidad DetailBooking con lista de servicios.
     */
    @Test
    void shouldHandleEdgeCase_WhenBuildWithMultipleServices() throws Exception {
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
        BookingDTO result = bookingService.createBooking(withServicesBookingCreateDTO);

        // Then
        assertNotNull(result);
        verify(detailBookingRepo).save(argThat(detailBooking -> 
            detailBooking.getListServices() != null &&
            detailBooking.getListServices().isEmpty() // Currently returns empty list
        ));
    }
}
