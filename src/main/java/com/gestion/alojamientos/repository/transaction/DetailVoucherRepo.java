package com.gestion.alojamientos.repository.transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.transaction.DetailVoucher;

@Repository
public interface DetailVoucherRepo extends JpaRepository<DetailVoucher, Long> {
    
    // CONSULTAS BÁSICAS
    
    /**
     * Encuentra DetailVoucher por Voucher ID
     */
    @Query("SELECT dv FROM DetailVoucher dv " +
           "WHERE dv.id IN (SELECT v.detailVoucher.id FROM Voucher v WHERE v.id = :voucherId)")
    Optional<DetailVoucher> findByVoucherId(@Param("voucherId") Long voucherId);
    
    // CONSULTAS PARA ANÁLISIS
    
    /**
     * Encuentra estadísticas de precios por noche
     */
    @Query("SELECT AVG(dv.priceNight), MIN(dv.priceNight), MAX(dv.priceNight) " +
           "FROM DetailVoucher dv " +
           "JOIN Voucher v ON dv.id = v.detailVoucher.id " +
           "WHERE v.voucherState = 'PAID' " +
           "AND v.creationDate BETWEEN :startDate AND :endDate")
    List<Object[]> findPriceStatistics(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Encuentra estadísticas de duración de estadía
     */
    @Query("SELECT AVG(dv.numberNights), MIN(dv.numberNights), MAX(dv.numberNights) " +
           "FROM DetailVoucher dv " +
           "JOIN Voucher v ON dv.id = v.detailVoucher.id " +
           "WHERE v.voucherState = 'PAID'")
    List<Object[]> findStayDurationStatistics();
}