package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.Host.DeleteHostDTO;
import com.gestion.alojamientos.dto.Host.HostCreateDTO;
import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.dto.Host.HostUpdateDTO;
import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;

import java.time.LocalDate;

public interface HostService {

    /**
     * Registra un nuevo Host en el sistema.
     */
    HostDTO registerHost(HostCreateDTO dto) throws RepeatedElementException, InvalidElementException;

    HostDTO loginHost(UserLoginDTO dto) throws ElementNotFoundException, InvalidElementException;

    /**
     * Edita los datos de un Host existente.
     */
    HostDTO editHost(Long id, HostUpdateDTO dto) throws ElementNotFoundException, InvalidElementException;

    /**
     * Elimina lógicamente un Host.
     */
    void deleteHost(Long id, DeleteHostDTO dto) throws ElementNotFoundException, InvalidElementException;

    /**
     * Obtiene un Host por su ID.
     */
    HostDTO getHostById(Long id) throws ElementNotFoundException;

    /**
     * Verifica si el Host es mayor de edad.
     */
    boolean isOfAge(LocalDate birthDate);

    /**
     * Genera un código de reseteo de contraseña y lo envía por email.
     */
    String generateResetCode(String email) throws ElementNotFoundException;

    /**
     * Cambia la contraseña del Host autenticado.
     */
    void changePassword(Long userId, ChangePasswordDto dto) throws InvalidElementException, ElementNotFoundException;

    /**
     * Restablece la contraseña del Host mediante un código enviado al correo.
     */
    void resetPassword(ResetPasswordDto dto) throws InvalidElementException, ElementNotFoundException;
}
