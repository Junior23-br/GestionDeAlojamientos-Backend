package com.gestion.alojamientos.service;

import java.util.List;

import com.gestion.alojamientos.dto.booking.*;

public interface BookingService{
    BookingDTO createBooking(BookingCreateDTO createBookingDTO) throws Exception;
    boolean  cancelBooking(DeleteBookingDTO cancelBookingDTO) throws Exception;
    List<BookingDTO> getGuestBookings(Long idGuest) throws Exception;
    List<BookingDTO> getAccommodationBookings(Long idAccommodation) throws Exception;
    BookingDTO updateBooking(BookingUpdateDTO updateBookingDTO) throws Exception;
}
