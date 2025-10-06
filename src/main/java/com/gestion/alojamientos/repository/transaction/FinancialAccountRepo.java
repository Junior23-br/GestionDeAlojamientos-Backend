package com.gestion.alojamientos.repository.transaction;

import com.gestion.alojamientos.model.transaction.FinancialAccount;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialAccountRepo extends JpaRepository<FinancialAccount, Long> {
        
    /**
     * Encuentra métodos de pago por Guest ID
     */
    List<FinancialAccount> findByCarHolderId(Long guestId);
    
    /**
     * Encuentra método de pago principal (primero)
     */
    @Query("SELECT fa FROM FinancialAccount fa " +
           "WHERE fa.carHolder.id = :guestId " +
           "ORDER BY fa.id ASC " +
           "LIMIT 1")
    Optional<FinancialAccount> findPrimaryPaymentMethod(@Param("guestId") Long guestId);
}