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
     * Encuentra Guests por estado
     */
    List<Guest> findByState(StatesOfGuest state);

    // -------------------- BÚSQUEDAS BÁSICAS --------------------

    Optional<Guest> findByEmail(String email);
    Optional<Guest> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    // -------------------- CONSULTAS CON RELACIONES --------------------

    /**
     * Guest con sus métodos de pago (FinancialAccount)
     */
    @Query("""
           SELECT DISTINCT g FROM Guest g
           LEFT JOIN FETCH g.paymentMethods
           WHERE g.id = :guestId
           """)
    Optional<Guest> findByIdWithPaymentMethods(@Param("guestId") Long guestId);

    /**
     * Guest con su lista básica de bookings
     */
    @Query("""
           SELECT DISTINCT g FROM Guest g
           LEFT JOIN FETCH g.bookingList
           WHERE g.id = :guestId
           """)
    Optional<Guest> findByIdWithBookings(@Param("guestId") Long guestId);

    /**
     * Guest con historial de transacciones
     */
    @Query("""
           SELECT DISTINCT g FROM Guest g
           LEFT JOIN FETCH g.transactionHistory
           WHERE g.id = :guestId
           """)
    Optional<Guest> findByIdWithTransactions(@Param("guestId") Long guestId);

    /**
     * Guest con perfil completo: métodos de pago, bookings y transacciones
     */
    @Query("""
           SELECT DISTINCT g FROM Guest g
           LEFT JOIN FETCH g.paymentMethods
           LEFT JOIN FETCH g.bookingList b
           LEFT JOIN FETCH b.accomodation a
           LEFT JOIN FETCH a.ubication
           LEFT JOIN FETCH g.transactionHistory t
           WHERE g.id = :guestId
           """)
    Optional<Guest> findByIdWithCompleteProfile(@Param("guestId") Long guestId);

    /**
     * Guest con bookings detallados (incluye services y serviceFee)
     */
    @Query("""
           SELECT DISTINCT g FROM Guest g
           LEFT JOIN FETCH g.bookingList b
           LEFT JOIN FETCH b.accomodation a
           LEFT JOIN FETCH a.ubication
           LEFT JOIN FETCH b.detailBooking d
           LEFT JOIN FETCH d.listServices
           LEFT JOIN FETCH d.serviceFee
           WHERE g.id = :guestId
           """)
    Optional<Guest> findByIdWithDetailedBookings(@Param("guestId") Long guestId);

    // -------------------- CONSULTAS DE ESTADO --------------------

    /**
     * Guest con bookings activos (confirmados o en proceso)
     */
    @Query("""
           SELECT DISTINCT g FROM Guest g
           LEFT JOIN FETCH g.bookingList b
           LEFT JOIN FETCH b.accomodation
           WHERE g.id = :guestId
           AND b.bookingState IN ('CONFIRMED', 'PENDING', 'CHECK_IN')
           """)
    Optional<Guest> findByIdWithActiveBookings(@Param("guestId") Long guestId);

    /**
     * Guest con bookings históricos (finalizados o cancelados)
     */
    @Query("""
           SELECT DISTINCT g FROM Guest g
           LEFT JOIN FETCH g.bookingList b
           LEFT JOIN FETCH b.accomodation
           WHERE g.id = :guestId
           AND b.bookingState IN ('CHECK_OUT', 'CANCELLED')
           """)
    Optional<Guest> findByIdWithHistoricalBookings(@Param("guestId") Long guestId);

    // -------------------- CONSULTAS ESTADÍSTICAS --------------------

    /**
     * Cantidad de bookings por estado
     */
    @Query("""
           SELECT b.bookingState, COUNT(b)
           FROM Booking b
           WHERE b.guest.id = :guestId
           GROUP BY b.bookingState
           """)
    List<Object[]> countBookingsByState(@Param("guestId") Long guestId);

    /**
     * Cantidad de transacciones asociadas al Guest
     */
    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           WHERE t.holder.id = :guestId
           """)
    Long countTransactionsByGuestId(@Param("guestId") Long guestId);

    /**
     * Total gastado por el Guest (solo reservas completadas o con check-out)
     */
    @Query("""
           SELECT COALESCE(SUM(b.totalPrice), 0)
           FROM Booking b
           WHERE b.guest.id = :guestId
           AND b.bookingState IN ('CHECK_OUT', 'CANCELLED')
           """)
    Double calculateTotalSpent(@Param("guestId") Long guestId);

    /**
     * Verifica si el Guest tiene reservas activas
     */
    @Query("""
           SELECT COUNT(b) > 0
           FROM Booking b
           WHERE b.guest.id = :guestId
           AND b.bookingState IN ('CONFIRMED', 'PENDING', 'CHECK_IN')
           """)
    boolean hasActiveBookings(@Param("guestId") Long guestId);

    // -------------------- CONSULTAS DE LISTADOS --------------------

//     List<Guest> findByState(StatesOfGuest state);

    /**
     * Guests con reservas recientes
     */
    @Query("""
           SELECT DISTINCT g FROM Guest g
           JOIN g.bookingList b
           WHERE b.creationDate >= :sinceDate
           ORDER BY b.creationDate DESC
           """)
    List<Guest> findWithRecentBookings(@Param("sinceDate") LocalDateTime sinceDate);
}
