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

    // CONSULTAS POR GUEST (HUÉSPED)
    
    /**
     * Encuentra vouchers por Guest ID
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "LEFT JOIN FETCH v.booking " +
           "WHERE v.guest.id = :guestId " +
           "ORDER BY v.creationDate DESC")
    List<Voucher> findByGuestId(@Param("guestId") Long guestId);
    
    /**
     * Encuentra vouchers por Guest ID paginados
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "WHERE v.guest.id = :guestId")
    Page<Voucher> findByGuestId(@Param("guestId") Long guestId, Pageable pageable);
    
    /**
     * Encuentra vouchers activos por Guest ID
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "LEFT JOIN FETCH v.booking " +
           "WHERE v.guest.id = :guestId " +
           "AND v.voucherState IN ('ACTIVE', 'PENDING') " +
           "ORDER BY v.creationDate DESC")
    List<Voucher> findActiveVouchersByGuestId(@Param("guestId") Long guestId);
    
    // CONSULTAS POR ESTADO
    
    /**
     * Encuentra vouchers por estado
     */
    List<Voucher> findByVoucherState(VoucherState voucherState);
    
    /**
     * Encuentra vouchers por estado con paginación
     */
    Page<Voucher> findByVoucherState(VoucherState voucherState, Pageable pageable);
    
    /**
     * Encuentra vouchers pendientes de pago
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.guest " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "WHERE v.voucherState = 'PENDING' " +
           "ORDER BY v.creationDate ASC")
    List<Voucher> findPendingVouchers();
    
    // CONSULTAS POR FECHAS
    
    /**
     * Encuentra vouchers por rango de fechas de creación
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.guest " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "WHERE v.creationDate BETWEEN :startDate AND :endDate " +
           "ORDER BY v.creationDate DESC")
    List<Voucher> findByCreationDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Encuentra vouchers creados en una fecha específica
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.guest " +
           "WHERE DATE(v.creationDate) = :date " +
           "ORDER BY v.creationDate DESC")
    List<Voucher> findByCreationDate(@Param("date") LocalDate date);
    
    // CONSULTAS POR BOOKING
    
    /**
     * Encuentra voucher por Booking ID
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "LEFT JOIN FETCH v.guest " +
           "LEFT JOIN FETCH v.paymentMethod " +
           "WHERE v.booking.id = :bookingId")
    Optional<Voucher> findByBookingId(@Param("bookingId") Long bookingId);
    
    /**
     * Verifica si existe voucher para un Booking
     */
    @Query("SELECT COUNT(v) > 0 FROM Voucher v WHERE v.booking.id = :bookingId")
    boolean existsByBookingId(@Param("bookingId") Long bookingId);
    
    // CONSULTAS COMPLETAS CON RELACIONES
    
    /**
     * Encuentra voucher por ID con todas las relaciones
     */
    @Query("SELECT DISTINCT v FROM Voucher v " +
           "LEFT JOIN FETCH v.guest " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "LEFT JOIN FETCH v.paymentMethod " +
           "LEFT JOIN FETCH v.booking b " +
           "LEFT JOIN FETCH b.accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE v.id = :id")
    Optional<Voucher> findByIdWithAllDetails(@Param("id") Long id);
    
    /**
     * Encuentra voucher por voucherID con detalles completos
     */
    @Query("SELECT DISTINCT v FROM Voucher v " +
           "LEFT JOIN FETCH v.guest " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "LEFT JOIN FETCH v.paymentMethod " +
           "LEFT JOIN FETCH v.booking " +
           "WHERE v.voucherID = :voucherID")
    Optional<Voucher> findByVoucherIDWithDetails(@Param("voucherID") String voucherID);
    
    // CONSULTAS DE ESTADÍSTICAS
    
    /**
     * Cuenta vouchers por estado
     */
    @Query("SELECT v.voucherState, COUNT(v) FROM Voucher v GROUP BY v.voucherState")
    List<Object[]> countVouchersByState();
    
    /**
     * Calcula total de ingresos por estado de voucher
     */
    @Query("SELECT v.voucherState, COALESCE(SUM(v.total), 0) FROM Voucher v GROUP BY v.voucherState")
    List<Object[]> calculateRevenueByVoucherState();
    
    /**
     * Calcula estadísticas de vouchers por Guest
     */
    @Query("SELECT v.guest.id, COUNT(v), COALESCE(SUM(v.total), 0) " +
           "FROM Voucher v " +
           "WHERE v.voucherState = 'PAID' " +
           "GROUP BY v.guest.id")
    List<Object[]> findVoucherStatsByGuest();
    
    /**
     * Calcula total de ingresos en un período
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Voucher v " +
           "WHERE v.voucherState = 'PAID' " +
           "AND v.creationDate BETWEEN :startDate AND :endDate")
    Double calculateTotalRevenueInPeriod(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    
    // CONSULTAS PARA REPORTES
    
    /**
     * Encuentra vouchers para reporte diario
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.guest " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "WHERE DATE(v.creationDate) = :date " +
           "ORDER BY v.creationDate DESC")
    List<Voucher> findDailyVouchers(@Param("date") LocalDate date);
    
    /**
     * Encuentra top vouchers por monto
     */
    @Query("SELECT v FROM Voucher v " +
           "LEFT JOIN FETCH v.guest " +
           "LEFT JOIN FETCH v.detailVoucher " +
           "WHERE v.voucherState = 'PAID' " +
           "ORDER BY v.total DESC")
    List<Voucher> findTopVouchersByAmount(Pageable pageable);
    
    // CONSULTAS DE VALIDACIÓN
    
    /**
     * Verifica si el guest tiene vouchers pendientes
     */
    @Query("SELECT COUNT(v) > 0 FROM Voucher v " +
           "WHERE v.guest.id = :guestId " +
           "AND v.voucherState = 'PENDING'")
    boolean hasPendingVouchers(@Param("guestId") Long guestId);
    
    /**
     * Verifica si el voucher puede ser aplicado (está activo y no expirado)
     */
    @Query("SELECT COUNT(v) > 0 FROM Voucher v " +
           "WHERE v.voucherID = :voucherID " +
           "AND v.voucherState = 'ACTIVE'")
    boolean isVoucherApplicable(@Param("voucherID") String voucherID);
}