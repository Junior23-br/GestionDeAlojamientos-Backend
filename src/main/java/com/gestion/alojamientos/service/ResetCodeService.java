package com.gestion.alojamientos.service;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.users.Guest;

public interface ResetCodeService {
    /**
     * Genera un código aleatorio, lo asigna al guest y lo envía por email
     * @param guest Huésped al que se le enviará el código
     * @return  código generado
     * @throws InvalidElementException si falla el envío
     */
    String generateAndSendCode(Guest guest) throws InvalidElementException;

    /**
     * Valida que el código proporcionado sea correcto y no esté expirado.
     * @param guest Huésped
     * @param code Código de recuperación
     * @return true si es válido
     * @throws InvalidElementException si el código es inválido o expiró
     */
    void validateCode(Guest guest, String code) throws InvalidElementException;
}

