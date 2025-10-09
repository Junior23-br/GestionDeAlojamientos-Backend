package com.gestion.alojamientos.dto.transaction;

/**
 * DTO utilizado para eliminar una transacción existente.
 */
public record DeleteTransactionDTO(
        String id // ID de la transacción a eliminar
) {}
