package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.BookingUpdateDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingUpdateDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.booking.DetailBooking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.repository.booking.DetailBookingRepo;
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
 * Clase de prueba para el método updateBooking del servicio BookingServiceImpl.
 * Prueba la funcionalidad de actualizar una reserva existente.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceUpdateBookingTest {

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private DetailBookingRepo detailBookingRepo;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private Booking updatedBooking;
    private BookingDTO bookingDTO;
    private BookingUpdateDTO validBookingUpdateDTO;
    private BookingUpdateDTO invalidBookingStateUpdateDTO;
    private BookingUpdateDTO nullBookingUpdateDTO;
    private DetailBookingUpdateDTO detailBookingUpdateDTO;
    private Long validBookingId;
    private Long invalidBookingId;

    @BeforeEach
    void setUp() {
        validBookingId = 1L;
        invalidBookingId = 99L;
        
        booking = new Booking();
        booking.setId(validBookingId);
        booking.setBookingState(StatesOfBooking.CONFIRMED);
        booking.setTotalPrice(200.0);
        booking.setPaymentStatus(true);
        booking.setCreationDate(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());

        updatedBooking = new Booking();
        updatedBooking.setId(validBookingId);
        updatedBooking.setBookingState(StatesOfBooking.CHECK_OUT);
        updatedBooking.setTotalPrice(250.0);
        updatedBooking.setPaymentStatus(true);
        updatedBooking.setCreationDate(LocalDateTime.now());
        updatedBooking.setUpdateTime(LocalDateTime.now());

        detailBookingUpdateDTO = new DetailBookingUpdateDTO(
                1L,                             // idDetailBooking
                LocalDate.now().plusDays(2),    // checkInDate
                LocalDate.now().plusDays(4),    // checkOutDate
                3,                              // numberOfGuest
                1L                              // serviceFeeId
        );

        validBookingUpdateDTO = new BookingUpdateDTO(
                "COMPLETED",                    // bookingState
                250.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingUpdateDTO,         // detailBookingUpdateDTO
                validBookingId                  // idBooking
        );

        invalidBookingStateUpdateDTO = new BookingUpdateDTO(
                "INVALID_STATE",                // bookingState
                250.0,                          // totalPrice
                true,                           // paymenStatus
                1L,                             // idPaymentMethod
                detailBookingUpdateDTO,         // detailBookingUpdateDTO
                validBookingId                  // idBooking
        );

        nullBookingUpdateDTO = new BookingUpdateDTO(
                null,                           // bookingState
                null,                           // totalPrice
                null,                           // paymenStatus
                null,                           // idPaymentMethod
                null,                           // detailBookingUpdateDTO
                invalidBookingId                // idBooking
        );

        DetailBookingDTO detailBookingDTO = new DetailBookingDTO(
                1L, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4),
                3, 125.0, 250.0, 0.0, null, null, 1L
        );

        bookingDTO = new BookingDTO(
                validBookingId, LocalDateTime.now(), LocalDateTime.now(), StatesOfBooking.CHECK_OUT,
                250.0, true, 1L, 1L, 1L, 1L, 1L, detailBookingDTO
        );
    }

    /**
     * Prueba el caso de éxito: actualizar una reserva existente con datos válidos.
     * Verifica que se actualicen correctamente los datos y se retorne el BookingDTO actualizado.
     */
    @Test
    void shouldReturnUpdatedBookingDTO_WhenUpdateIsSuccessful() throws Exception {
        // Given
        when(bookingRepo.findById(validBookingId)).thenReturn(Optional.of(booking));
        when(detailBookingRepo.save(any(DetailBooking.class))).thenReturn(new DetailBooking());
        when(bookingRepo.save(any(Booking.class))).thenReturn(updatedBooking);
        when(bookingMapper.toDto(updatedBooking)).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.updateBooking(validBookingUpdateDTO);

        // Then
        assertNotNull(result);
        assertEquals(validBookingId, result.id());
        assertEquals(StatesOfBooking.CHECK_OUT, result.bookingState());
        assertEquals(250.0, result.totalPrice());
        verify(bookingRepo).findById(validBookingId);
        verify(detailBookingRepo).save(any(DetailBooking.class));
        verify(bookingRepo).save(any(Booking.class));
        verify(bookingMapper).toDto(updatedBooking);
    }

    /**
     * Prueba el caso de fracaso: cuando la reserva no existe.
     * Verifica que se lance EntityNotFoundException cuando el ID no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenBookingDoesNotExist() {
        // Given
        when(bookingRepo.findById(invalidBookingId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.updateBooking(nullBookingUpdateDTO)
        );

        assertEquals("Reserva no encontrada", exception.getMessage());
        verify(bookingRepo).findById(invalidBookingId);
        verifyNoInteractions(detailBookingRepo);
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso de datos inválidos: estado de reserva inválido.
     * Verifica que se lance IllegalArgumentException cuando el estado de reserva es inválido.
     */
    @Test
    void shouldHandleInvalidData_WhenBookingStateIsInvalid() {
        // Given
        when(bookingRepo.findById(validBookingId)).thenReturn(Optional.of(booking));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateBooking(invalidBookingStateUpdateDTO)
        );

        assertEquals("Estado de reserva no válido: INVALID_STATE", exception.getMessage());
        verify(bookingRepo).findById(validBookingId);
        verifyNoInteractions(detailBookingRepo);
        verifyNoInteractions(bookingMapper);
    }

    /**
     * Prueba el caso edge: cuando todos los campos son nulos.
     * Verifica que se maneje correctamente cuando todos los campos de actualización son nulos.
     */
    @Test
    void shouldHandleEdgeCase_WhenAllFieldsAreNull() throws Exception {
        // Given
        BookingUpdateDTO nullFieldsUpdateDTO = new BookingUpdateDTO(
                null,                           // bookingState
                null,                           // totalPrice
                null,                           // paymenStatus
                null,                           // idPaymentMethod
                null,                           // detailBookingUpdateDTO
                validBookingId                  // idBooking
        );
        when(bookingRepo.findById(validBookingId)).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.updateBooking(nullFieldsUpdateDTO);

        // Then
        assertNotNull(result);
        assertEquals(validBookingId, result.id());
        verify(bookingRepo).findById(validBookingId);
        verify(bookingRepo).save(any(Booking.class));
        verify(bookingMapper).toDto(booking);
        verifyNoInteractions(detailBookingRepo);
    }
}
