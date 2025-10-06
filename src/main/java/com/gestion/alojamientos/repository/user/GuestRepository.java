package com.gestion.alojamientos.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
*Repositorio para gestionar operaciones de acceso a datos sobre la entidad, fundamental en la capa de persistencia
* Extiende de JPA para proporcionar metodos CRUD basicos(guardar,buscar,editar,eliminar)
* JPASpecificationExecutor permite ejecutar consultas avanzadas y dinamicas usando la API de especificaciones de JPA,
* ideal para filtros personalizados
 */
@Repository
public interface GuestRepository extends JpaRepository<Guest, Long>, JpaSpecificationExecutor<Guest> {

     /**
      * Busca un huésped a partir del correo registrado.
      * @param email correo del huésped a buscar
      * @return un Optional con el huésped si existe, vacío en caso contrario
      */
     Optional<Guest> findByEmail(String email);
     /**
     *Buscar un huesped por su numero de phoneNumber
      *@param phoneNumber  telefono del huesped a buscar
      * @return un Optional con el huésped si existe, vacío en caso contrario
      */
     Optional<Guest> findByPhoneNumber(String phoneNumber);
     /**
     *Buscar huesped por su firstName
      * @param firstName nombre del huesped
      * @return un Optional con el huésped si existe, vacío en caso contrario
      */
     Optional<Guest> findByFirstName(String firstName);
     /**
     *Buscar un huesped por su lastName
      * @param lastName apellido del huesped
      * @return un Optional con el huésped si existe, vacío en caso contrario
      */
     Optional<Guest> findByLastName(String lastName);
     /**
      * Verifica si existe un huesped con el correo especificado
      * @param email correo electronico a verificar
      * @return true si existe
      */
     boolean existsByEmail(String email);
 ;
     /**
      * Verifica si un huesped existe con el numero de phoneNumber especi   ficado
      * @param phoneNumber numero telefonico a verificar
      * @return true si existe, falso si no existe
      */
    boolean existsByPhoneNumber(String phoneNumber);


    // INFORMACIÓN BÁSICA DEL GUEST CON RELACIONES PRINCIPALES

     /**
     * Encuentra Guest por ID con métodos de pago
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
           "LEFT JOIN FETCH g.paymentMethods " +
           "WHERE g.id = :guestId")
    Optional<Guest> findByIdWithPaymentMethods(@Param("guestId") Long guestId);
    
    /**
     * Encuentra Guest por ID con bookings básicos
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
           "LEFT JOIN FETCH g.bookingList " +
           "WHERE g.id = :guestId")
    Optional<Guest> findByIdWithBookings(@Param("guestId") Long guestId);
    
    /**
     * Encuentra Guest por ID con historial de transacciones
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
           "LEFT JOIN FETCH g.transactionHistory " +
           "WHERE g.id = :guestId")
    Optional<Guest> findByIdWithTransactions(@Param("guestId") Long guestId);
    
    // INFORMACIÓN COMPUESTA PARA PERFIL/DASHBOARD
    
    /**
     * Encuentra Guest por ID con información completa para perfil
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
           "LEFT JOIN FETCH g.paymentMethods " +
           "LEFT JOIN FETCH g.bookingList b " +
           "LEFT JOIN FETCH b.accomodation " +
           "LEFT JOIN FETCH g.transactionHistory t " +
           "LEFT JOIN FETCH t.voucherList " +
           "WHERE g.id = :guestId")
    Optional<Guest> findByIdWithCompleteProfile(@Param("guestId") Long guestId);
    
    /**
     * Encuentra Guest por ID con bookings y detalles completos
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
           "LEFT JOIN FETCH g.bookingList b " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "LEFT JOIN FETCH b.detaillBooking d " +
           "LEFT JOIN FETCH d.listServices " +
           "WHERE g.id = :guestId")
    Optional<Guest> findByIdWithDetailedBookings(@Param("guestId") Long guestId);
    
    // CONSULTAS ESPECÍFICAS PARA BOOKINGS
    
    /**
     * Encuentra Guest por ID con bookings activos/reservados
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
           "LEFT JOIN FETCH g.bookingList b " +
           "LEFT JOIN FETCH b.accomodation " +
           "WHERE g.id = :guestId " +
           "AND b.bookingState IN ('CONFIRMED', 'PENDING', 'ACTIVE')")
    Optional<Guest> findByIdWithActiveBookings(@Param("guestId") Long guestId);
    
    /**
     * Encuentra Guest por ID con bookings históricos/completados
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
           "LEFT JOIN FETCH g.bookingList b " +
           "LEFT JOIN FETCH b.accomodation " +
           "WHERE g.id = :guestId " +
           "AND b.bookingState IN ('COMPLETED', 'CANCELLED')")
    Optional<Guest> findByIdWithHistoricalBookings(@Param("guestId") Long guestId);
    
    // CONSULTAS PARA ESTADÍSTICAS Y DASHBOARD
    
    /**
     * Cuenta bookings por estado para un Guest
     */
    @Query("SELECT b.bookingState, COUNT(b) FROM Booking b " +
           "WHERE b.guest.id = :guestId " +
           "GROUP BY b.bookingState")
    List<Object[]> countBookingsByState(@Param("guestId") Long guestId);
    
    /**
     * Cuenta transacciones por Guest
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.holder.id = :guestId")
    Long countTransactionsByGuestId(@Param("guestId") Long guestId);
    
    /**
     * Calcula gasto total del Guest
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b " +
           "WHERE b.guest.id = :guestId " +
           "AND b.bookingState = 'COMPLETED'")
    Double calculateTotalSpent(@Param("guestId") Long guestId);
    
    
    /**
     * Verifica si Guest tiene bookings activos
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.guest.id = :guestId " +
           "AND b.bookingState IN ('CONFIRMED', 'PENDING', 'ACTIVE')")
    boolean hasActiveBookings(@Param("guestId") Long guestId);
    
    // CONSULTAS PARA LISTADOS
    
    /**
     * Encuentra Guests por estado
     */
    List<Guest> findByState(StatesOfGuest state);
    
    /**
     * Encuentra Guests con bookings recientes
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
           "JOIN g.bookingList b " +
           "WHERE b.creationDate >= :sinceDate " +
           "ORDER BY b.creationDate DESC")
    List<Guest> findWithRecentBookings(@Param("sinceDate") LocalDateTime sinceDate);

}
