package com.gestion.alojamientos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.alojamientos.model.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

     /**
      * Busca una transacción por su identificador único.
      * @param id Identificador de la transacción.
      * @return Un Optional con la transacción encontrada, o vacío si no existe.
      */
     Optional<Transaction> findById(Long id);

     /**
      * Busca todas las transacciones asociadas a un identificador de reserva específico.
      * @param bookingId Identificador de la reserva.
      * @return Un Optional con la lista de transacciones encontradas, o vacío si no existen.
      */
     Optional<List<Transaction>> findByBookingId(Long bookingId);

     /**
      * Verifica si existe una transacción con el identificador dado.
      * @param id Identificador de la transacción.
      * @return true si existe, false en caso contrario.
      */
     boolean existsById(Long id);
}