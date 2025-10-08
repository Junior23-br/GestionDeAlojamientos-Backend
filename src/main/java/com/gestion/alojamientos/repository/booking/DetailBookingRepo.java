package com.gestion.alojamientos.repository.booking;

import java.time.LocalDate;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.booking.DetailBooking;

@Repository
public interface DetailBookingRepo extends JpaRepository<DetailBooking, Long> {
    
    // CONSULTAS BÁSICAS
    
    /**
     * Encuentra DetailBooking por Booking ID
     */
    @Query("SELECT d FROM DetailBooking d " +
           "LEFT JOIN FETCH d.listServices " +
           "LEFT JOIN FETCH d.serviceFee " +
           "WHERE d.booking.id = :bookingId")
    Optional<DetailBooking> findByBookingId(@Param("bookingId") Long bookingId);
    
    /**
     * Encuentra DetailBookings por rango de fechas
     */
    @Query("SELECT d FROM DetailBooking d " +
           "JOIN d.booking b " +
           "WHERE d.checkInDate BETWEEN :startDate AND :endDate " +
           "AND b.bookingState IN ('CONFIRMED', 'ACTIVE')")
    List<DetailBooking> findByCheckInDateRange(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    // CONSULTAS PARA REPORTES
    
    /**
     * Calcula estadísticas de ocupación por Accommodation
     */
    @Query("SELECT d.booking.accomodation.id, " +
           "COUNT(d), " +
           "AVG(d.numberOfGuest), " +
           "AVG(d.subTotal) " +
           "FROM DetailBooking d " +
           "JOIN d.booking b " +
           "WHERE b.bookingState = 'COMPLETED' " +
           "AND d.checkInDate BETWEEN :startDate AND :endDate " +
           "GROUP BY d.booking.accomodation.id")
    List<Object[]> findOccupancyStatsByAccommodation(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
    
    /**
     * Encuentra estadísticas de duración de estadía
     */
    @Query("SELECT " +
           "FUNCTION('DATEDIFF', d.checkOutDate, d.checkInDate) as stayDuration, " +
           "COUNT(d), " +
           "AVG(d.subTotal) " +
           "FROM DetailBooking d " +
           "JOIN d.booking b " +
           "WHERE b.bookingState = 'COMPLETED' " +
           "AND d.checkInDate BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATEDIFF', d.checkOutDate, d.checkInDate)")
    List<Object[]> findStayDurationStats(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
}