package com.gestion.alojamientos.repository.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestion.alojamientos.model.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

      /**
     * Busca una transacción por su identificador único.
     */
    Optional<Transaction> findById(Long id);

    /**
     * Busca todas las transacciones asociadas a un Booking ID 
     * (vinculadas indirectamente a través del Voucher).
     */
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN FETCH t.voucher v " +
           "LEFT JOIN FETCH v.booking b " +
           "WHERE b.id = :bookingId")
    List<Transaction> findByBookingId(@Param("bookingId") Long bookingId);

    /**
     * Verifica si existe una transacción con el identificador dado.
     */
    boolean existsById(Long id);
}