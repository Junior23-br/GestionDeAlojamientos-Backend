package com.gestion.alojamientos.repository.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;

public interface BookingRepo extends JpaRepository<Booking, Long> {

     /**
      * Busca una reserva por su identificador único.
      * @param id Identificador de la reserva.
      * @return Un Optional con la reserva encontrada, o vacío si no existe.
      */
     Optional<Booking> findById(Long id);

     /**
      * Busca una reserva por el identificador del alojamiento.
      * @param accomodationId Identificador del alojamiento.
      * @return Un Optional con la reserva encontrada, o vacío si no existe.
      */
     Optional<Booking> findByAccomodationId(Long accomodationId);

     /**
      * Busca todas las reservas asociadas a un huésped específico.
      * @param guestId Identificador del huésped.
      * @return Un Optional con la lista de reservas encontradas, o vacío si no existen.
      */
     Optional<List<Booking>> findByGuestId(Long guestId);


     /**
      * Verifica si existe una reserva con el identificador dado.
      * @param id Identificador de la reserva.
      * @return true si existe, false en caso contrario.
      */
     boolean existsById(Long id);

     /**
      * Verifica si existe una reserva para el alojamiento dado.
      * @param accomodationId Identificador del alojamiento.
      * @return true si existe, false en caso contrario.
      */
     boolean existsByAccomodationId(Long accomodationId);

     /**
      * Verifica si existe una reserva para el huésped dado.
      * @param guestId Identificador del huésped.
      * @return true si existe, false en caso contrario.
      */
     boolean existsByGuestId(Long guestId);


      /**
     * Encuentra Bookings por Guest ID con información completa
     */
    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "LEFT JOIN FETCH b.detaillBooking d " +
           "LEFT JOIN FETCH d.listServices " +
           "LEFT JOIN FETCH b.voucher " +
           "WHERE b.guest.id = :guestId " +
           "ORDER BY b.creationDate DESC")
    List<Booking> findByGuestIdWithDetails(@Param("guestId") Long guestId);
    
    /**
     * Encuentra Bookings por Guest ID paginados
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE b.guest.id = :guestId")
    Page<Booking> findByGuestId(@Param("guestId") Long guestId, Pageable pageable);
    
    /**
     * Encuentra Bookings activos por Guest ID
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.accomodation " +
           "WHERE b.guest.id = :guestId " +
           "AND b.bookingState IN ('CONFIRMED', 'PENDING', 'ACTIVE') " +
           "ORDER BY b.detaillBooking.checkInDate ASC")
    List<Booking> findActiveBookingsByGuestId(@Param("guestId") Long guestId);
    
    /**
     * Encuentra próximo booking del Guest
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE b.guest.id = :guestId " +
           "AND b.bookingState = 'CONFIRMED' " +
           "AND b.detaillBooking.checkInDate >= CURRENT_DATE " +
           "ORDER BY b.detaillBooking.checkInDate ASC " +
           "LIMIT 1")
    Optional<Booking> findNextBookingByGuestId(@Param("guestId") Long guestId);

    /**
     * Encuentra bookings históricos por Guest ID
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE b.guest.id = :guestId " +
           "AND b.bookingState IN ('COMPLETED', 'CANCELLED') " +
           "ORDER BY b.detaillBooking.checkOutDate DESC")
    List<Booking> findHistoricalBookingsByGuestId(@Param("guestId") Long guestId);
    
    // CONSULTAS POR ACCOMMODATION Y HOST
    
    /**
     * Encuentra bookings por Accommodation ID
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.guest " +
           "WHERE b.accomodation.id = :accommodationId " +
           "ORDER BY b.detaillBooking.checkInDate DESC")
    List<Booking> findByAccommodationId(@Param("accommodationId") Long accommodationId);
    
    /**
     * Encuentra bookings por Host ID
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.guest " +
           "LEFT JOIN FETCH b.accomodation a " +
           "WHERE a.host.id = :hostId " +
           "ORDER BY b.creationDate DESC")
    List<Booking> findByHostId(@Param("hostId") Long hostId);
    
    /**
     * Encuentra bookings por Host ID paginados
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.guest " +
           "LEFT JOIN FETCH b.accomodation a " +
           "WHERE a.host.id = :hostId")
    Page<Booking> findByHostId(@Param("hostId") Long hostId, Pageable pageable);
    
    // CONSULTAS POR ESTADO Y FECHAS
    
    /**
     * Encuentra bookings por estado
     */
    List<Booking> findByBookingState(StatesOfBooking bookingState);
    
    /**
     * Encuentra bookings por rango de fechas de check-in
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE b.detaillBooking.checkInDate BETWEEN :startDate AND :endDate " +
           "AND b.bookingState IN ('CONFIRMED', 'ACTIVE')")
    List<Booking> findBookingsByCheckInDateRange(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
    
    /**
     * Encuentra bookings por rango de fechas de creación
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.accomodation " +
           "WHERE b.creationDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsByCreationDateRange(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
    
    // CONSULTAS DE DISPONIBILIDAD
    
    /**
     * Verifica disponibilidad de Accommodation en fechas específicas
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.accomodation.id = :accommodationId " +
           "AND b.bookingState IN ('CONFIRMED', 'ACTIVE', 'PENDING') " +
           "AND ((b.detaillBooking.checkInDate BETWEEN :checkIn AND :checkOut) " +
           "OR (b.detaillBooking.checkOutDate BETWEEN :checkIn AND :checkOut) " +
           "OR (b.detaillBooking.checkInDate <= :checkIn AND b.detaillBooking.checkOutDate >= :checkOut))")
    boolean isAccommodationBooked(@Param("accommodationId") Long accommodationId,
                                 @Param("checkIn") LocalDate checkIn,
                                 @Param("checkOut") LocalDate checkOut);
    
    /**
     * Encuentra bookings que se superponen con un rango de fechas
     */
    @Query("SELECT b FROM Booking b " +
           "WHERE b.accomodation.id = :accommodationId " +
           "AND b.bookingState IN ('CONFIRMED', 'ACTIVE') " +
           "AND ((b.detaillBooking.checkInDate BETWEEN :checkIn AND :checkOut) " +
           "OR (b.detaillBooking.checkOutDate BETWEEN :checkIn AND :checkOut) " +
           "OR (b.detaillBooking.checkInDate <= :checkIn AND b.detaillBooking.checkOutDate >= :checkOut))")
    List<Booking> findOverlappingBookings(@Param("accommodationId") Long accommodationId,
                                         @Param("checkIn") LocalDate checkIn,
                                         @Param("checkOut") LocalDate checkOut);
    
    // CONSULTAS DE ESTADÍSTICAS
    
    /**
     * Cuenta bookings por estado para un Guest
     */
    @Query("SELECT b.bookingState, COUNT(b) FROM Booking b " +
           "WHERE b.guest.id = :guestId " +
           "GROUP BY b.bookingState")
    List<Object[]> countBookingsByStateForGuest(@Param("guestId") Long guestId);
    
    /**
     * Cuenta bookings por estado para un Host
     */
    @Query("SELECT b.bookingState, COUNT(b) FROM Booking b " +
           "JOIN b.accomodation a " +
           "WHERE a.host.id = :hostId " +
           "GROUP BY b.bookingState")
    List<Object[]> countBookingsByStateForHost(@Param("hostId") Long hostId);
    
    /**
     * Calcula ingresos totales por Host
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b " +
           "JOIN b.accomodation a " +
           "WHERE a.host.id = :hostId " +
           "AND b.bookingState = 'COMPLETED' " +
           "AND b.paymentStatus = true")
    Double calculateTotalRevenueByHost(@Param("hostId") Long hostId);
    
    // CONSULTAS COMPLETAS CON TODAS LAS RELACIONES
    
    /**
     * Encuentra booking por ID con todas las relaciones
     */
    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN FETCH b.guest " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "LEFT JOIN FETCH a.host " +
           "LEFT JOIN FETCH b.detaillBooking d " +
           "LEFT JOIN FETCH d.listServices " +
           "LEFT JOIN FETCH d.serviceFee " +
           "LEFT JOIN FETCH b.paymentMethod " +
           "LEFT JOIN FETCH b.voucher " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithAllDetails(@Param("id") Long id);
    
    /**
     * Encuentra booking por ID con información básica para confirmación
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.guest " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "LEFT JOIN FETCH b.detaillBooking " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithConfirmationDetails(@Param("id") Long id);
    
    // CONSULTAS DE VALIDACIÓN
    
    /**
     * Verifica si existe booking activo para Accommodation y Guest
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.accomodation.id = :accommodationId " +
           "AND b.guest.id = :guestId " +
           "AND b.bookingState IN ('CONFIRMED', 'ACTIVE', 'PENDING')")
    boolean hasActiveBookingForAccommodationAndGuest(@Param("accommodationId") Long accommodationId,
                                                    @Param("guestId") Long guestId);
    
    /**
     * Verifica si el Guest tiene bookings activos
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.guest.id = :guestId " +
           "AND b.bookingState IN ('CONFIRMED', 'ACTIVE', 'PENDING')")
    boolean hasActiveBookings(@Param("guestId") Long guestId);

}
