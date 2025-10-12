package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for AdminServiceImpl.getGuestBookingsHistory method.
 * Covers success, not found, null repository/mapper, and internal failure cases.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetGuestBookingsHistoryTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Booking booking;
    private BookingDTO bookingDTO;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(1L);

        bookingDTO = new BookingDTO(
                1L,                                   // id
                LocalDateTime.now(),                  // creationDate
                LocalDateTime.now(),                  // updateTime
                StatesOfBooking.CONFIRMED,            // bookingState
                150.0,                                // totalPrice
                true,                                 // paymentStatus
                10L,                                  // paymentMethodId
                5L,                                   // guestId
                20L,                                  // detailBookingId
                30L,                                  // voucherId
                40L,                                  // accommodationId
                new DetailBookingDTO(
                        20L,                          // id
                        LocalDate.now(),              // checkInDate
                        LocalDate.now().plusDays(3),  // checkOutDate
                        2,                            // numberOfGuest
                        75.0,                         // priceNightAccommodation
                        225.0,                        // subTotal
                        0.0,                          // discount
                        null,                         // serviceFeeDTO (puede ser null para pruebas)
                        List.of(),                    // listServices (lista vacía para el test)
                        1L                            // idBooking
                )
        );

    }

    /**
     * Test 1: Éxito - Devuelve lista de reservas del huésped correctamente
     */
    @Test
    void getGuestBookingsHistory_Success_ShouldReturnListOfBookings() {
        // Arrange
        when(guestRepository.existsById(1L)).thenReturn(true);
        when(bookingRepo.findByGuestIdWithDetails(1L)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDTO);

        // Act
        List<BookingDTO> result = adminService.getGuestBookingsHistory(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(bookingDTO, result.get(0));
        verify(guestRepository).existsById(1L);
        verify(bookingRepo).findByGuestIdWithDetails(1L);
        verify(bookingMapper).toDto(booking);
    }

    /**
     * Test 2: Fracaso - Huésped no encontrado
     */
    @Test
    void getGuestBookingsHistory_GuestNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(guestRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getGuestBookingsHistory(99L)
        );

        assertEquals("Huésped no encontrado con ID: 99", exception.getMessage());
        verify(guestRepository).existsById(99L);
        verify(bookingRepo, never()).findByGuestIdWithDetails(anyLong());
    }

    /**
     * Test 3: Repositorio o mapper nulo - Lanza UnsupportedOperationException
     */
    @Test
    void getGuestBookingsHistory_NullReposOrMapper_ShouldThrowUnsupportedOperationException() {
        // Arrange
        AdminServiceImpl serviceWithNulls = new AdminServiceImpl();
        // bookingRepo y bookingMapper no se inyectan manualmente

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> serviceWithNulls.getGuestBookingsHistory(1L)
        );

        assertTrue(exception.getMessage().contains("BookingRepo o BookingMapper no están disponibles"));
    }

    /**
     * Test 4: Error interno - Falla al obtener reservas del huésped
     */
    @Test
    void getGuestBookingsHistory_InternalError_ShouldPropagateRuntimeException() {
        // Arrange
        when(guestRepository.existsById(1L)).thenReturn(true);
        when(bookingRepo.findByGuestIdWithDetails(1L)).thenThrow(new RuntimeException("Error interno al consultar"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.getGuestBookingsHistory(1L)
        );

        assertTrue(exception.getMessage().contains("Error interno al consultar"));
        verify(guestRepository).existsById(1L);
        verify(bookingRepo).findByGuestIdWithDetails(1L);
    }
}
