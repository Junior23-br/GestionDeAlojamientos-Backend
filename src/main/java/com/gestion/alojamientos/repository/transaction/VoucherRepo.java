package com.gestion.alojamientos.repository.transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.transaction.Voucher;
import com.gestion.alojamientos.model.transaction.VoucherState;

@Repository
public interface VoucherRepo extends JpaRepository<Voucher, Long> {


       // === CONSULTAS POR GUEST ===

    @Query("""
        SELECT v FROM Voucher v
        LEFT JOIN FETCH v.detailVoucher
        LEFT JOIN FETCH v.booking
        WHERE v.guest.id = :guestId
        ORDER BY v.creationDate DESC
    """)
    List<Voucher> findByGuestId(@Param("guestId") Long guestId);

    @Query("""
        SELECT v FROM Voucher v
        WHERE v.guest.id = :guestId
    """)
    Page<Voucher> findByGuestId(@Param("guestId") Long guestId, Pageable pageable);

    @Query("""
        SELECT v FROM Voucher v
        LEFT JOIN FETCH v.detailVoucher
        LEFT JOIN FETCH v.booking
        WHERE v.guest.id = :guestId
        AND v.voucherState IN (:states)
        ORDER BY v.creationDate DESC
    """)
    List<Voucher> findActiveVouchersByGuestId(
        @Param("guestId") Long guestId,
        @Param("states") List<VoucherState> states
    );

    // === CONSULTAS POR ESTADO ===

    List<Voucher> findByVoucherState(VoucherState voucherState);
    Page<Voucher> findByVoucherState(VoucherState voucherState, Pageable pageable);

    @Query("""
        SELECT v FROM Voucher v
        LEFT JOIN FETCH v.guest
        LEFT JOIN FETCH v.detailVoucher
        WHERE v.voucherState = 'PENDING'
        ORDER BY v.creationDate ASC
    """)
    List<Voucher> findPendingVouchers();

    // === CONSULTAS POR FECHAS ===

    @Query("""
        SELECT v FROM Voucher v
        LEFT JOIN FETCH v.guest
        LEFT JOIN FETCH v.detailVoucher
        WHERE v.creationDate BETWEEN :startDate AND :endDate
        ORDER BY v.creationDate DESC
    """)
    List<Voucher> findByCreationDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT v FROM Voucher v
        LEFT JOIN FETCH v.guest
        WHERE FUNCTION('DATE', v.creationDate) = :date
        ORDER BY v.creationDate DESC
    """)
    List<Voucher> findByCreationDate(@Param("date") LocalDate date);

    // === CONSULTAS POR BOOKING ===

    @Query("""
        SELECT v FROM Voucher v
        LEFT JOIN FETCH v.detailVoucher
        LEFT JOIN FETCH v.guest
        LEFT JOIN FETCH v.paymentMethod
        WHERE v.booking.id = :bookingId
    """)
    Optional<Voucher> findByBookingId(@Param("bookingId") Long bookingId);

    @Query("SELECT COUNT(v) > 0 FROM Voucher v WHERE v.booking.id = :bookingId")
    boolean existsByBookingId(@Param("bookingId") Long bookingId);

    // === CONSULTAS COMPLETAS ===

    @Query("""
        SELECT DISTINCT v FROM Voucher v
        LEFT JOIN FETCH v.guest
        LEFT JOIN FETCH v.detailVoucher
        LEFT JOIN FETCH v.paymentMethod
        LEFT JOIN FETCH v.booking b
        LEFT JOIN FETCH b.accomodation a
        LEFT JOIN FETCH a.ubication
        WHERE v.id = :id
    """)
    Optional<Voucher> findByIdWithAllDetails(@Param("id") Long id);

    // === ESTADÍSTICAS ===

    @Query("SELECT v.voucherState, COUNT(v) FROM Voucher v GROUP BY v.voucherState")
    List<Object[]> countVouchersByState();

    @Query("SELECT v.voucherState, COALESCE(SUM(v.total), 0) FROM Voucher v GROUP BY v.voucherState")
    List<Object[]> calculateRevenueByVoucherState();

    @Query("""
    SELECT v.guest.id, COUNT(v), COALESCE(SUM(v.total), 0)
    FROM Voucher v
    WHERE v.voucherState = :state
    GROUP BY v.guest.id
    """)
    List<Object[]> findVoucherStatsByGuest(@Param("state") VoucherState state);

    @Query("""
        SELECT COALESCE(SUM(v.total), 0)
        FROM Voucher v
        WHERE v.voucherState = :state
        AND v.creationDate BETWEEN :startDate AND :endDate
    """)
    Double calculateTotalRevenueInPeriod(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("state") VoucherState state
    );
    // === REPORTES ===

    @Query("""
        SELECT v FROM Voucher v
        LEFT JOIN FETCH v.guest
        LEFT JOIN FETCH v.detailVoucher
        WHERE FUNCTION('DATE', v.creationDate) = :date
        ORDER BY v.creationDate DESC
    """)
    List<Voucher> findDailyVouchers(@Param("date") LocalDate date);

    @Query("""
        SELECT v FROM Voucher v
        LEFT JOIN FETCH v.guest
        LEFT JOIN FETCH v.detailVoucher
        WHERE v.voucherState = :state
        ORDER BY v.total DESC
    """)
    List<Voucher> findTopVouchersByAmount(@Param("state") VoucherState state, Pageable pageable);

    // === VALIDACIÓN ===

    @Query("""
        SELECT COUNT(v) > 0
        FROM Voucher v
        WHERE v.guest.id = :guestId
        AND v.voucherState = :state
    """)
    boolean hasPendingVouchers(@Param("guestId") Long guestId,
                            @Param("state") VoucherState state);

    
}