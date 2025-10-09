package com.gestion.alojamientos.service.Impl;

import com.gestion.alojamientos.dto.Host.DeleteHostDTO;
import com.gestion.alojamientos.dto.Host.HostCreateDTO;
import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.dto.Host.HostUpdateDTO;
import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.mapper.users.HostMapper;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.HostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@Transactional
public class HostServiceImpl implements HostService {

    @Autowired
    private HostRepo hostRepository;

    @Autowired
    private HostMapper hostMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // TODO: Implementar estos servicios cuando se tenga notificación y manejo de código de reseteo
    @Autowired(required = false)
    private ResetCodeServiceImpl resetCodeServiceImpl;
    @Autowired(required = false)
    private EmailServiceImpl emailServiceImpl;

    /**
     * Registra un nuevo Host en el sistema.
     */
    @Override
    public HostDTO registerHost(HostCreateDTO dto) throws RepeatedElementException, InvalidElementException {
        if (hostRepository.existsByEmail(dto.email())) {
            throw new RepeatedElementException("El correo electrónico ya está registrado.");
        }
        if (dto.birthDate().isAfter(LocalDate.now())) {
            throw new InvalidElementException("La fecha de nacimiento es inválida.");
        }
        if (!isOfAge(dto.birthDate())) {
            throw new InvalidElementException("Debes ser mayor de edad para registrarte como Host.");
        }

        Host host = hostMapper.toEntity(dto);
        host.setPassword(passwordEncoder.encode(dto.password()));
        host.setStatus(StatesOfHost.ACTIVE);

        hostRepository.save(host);
        return hostMapper.toDTO(host);
    }

    /**
     * Edita los datos de un Host existente.
     */
    @Override
    public HostDTO editHost(Long id, HostUpdateDTO dto) throws ElementNotFoundException, InvalidElementException {
        Host host = hostRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Host no encontrado con ID: " + id));

        if (host.getStatus() == StatesOfHost.DELETED) {
            throw new ElementNotFoundException("El perfil ha sido eliminado y no puede editar la información.");
        }


        // Actualizar datos con el mapper
        host.setName(dto.name());
        host.setPhoneNumber(dto.phoneNumber());
        host.setPersonalDescription(dto.personalDescription());
        host.setUrlProfilePhoto(dto.urlProfilePhoto());

        return hostMapper.toDTO(hostRepository.save(host));
    }

    /**
     * Elimina lógicamente un Host (cambia su estado a DELETED).
     */
    @Override
    public void deleteHost(Long id, DeleteHostDTO dto) throws ElementNotFoundException, InvalidElementException {
        Host host = hostRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Host no encontrado con ID: " + id));

        if (!passwordEncoder.matches(dto.password(), host.getPassword())) {
            throw new InvalidElementException("Contraseña incorrecta.");
        }

        host.setStatus(StatesOfHost.DELETED);
        hostRepository.save(host);
    }

    /**
     * Obtiene un Host por su ID.
     */
    @Override
    public HostDTO getHostById(Long id) throws ElementNotFoundException {
        Host host = hostRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Host no encontrado con ID: " + id));
        return hostMapper.toDTO(host);
    }

    /**
     * Verifica si el Host es mayor de edad.
     */
    @Override
    public boolean isOfAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        Period period = Period.between(birthDate, today);
        return period.getYears() >= 18;
    }

    /**
     * Genera y envía un código de reseteo de contraseña.
     * TODO: Implementar cuando se integre el sistema de notificaciones.
     */
    @Override
    public String generateResetCode(String email) throws ElementNotFoundException {
        // TODO: Implementar una vez se integre ResetCodeServiceImpl y EmailServiceImpl
        throw new UnsupportedOperationException("Funcionalidad pendiente de implementación.");
    }

    /**
     * Cambia la contraseña de un Host.
     * TODO: Implementar validación de la contraseña actual y actualización segura.
     */
    @Override
    public void changePassword(Long userId, ChangePasswordDto dto) throws InvalidElementException, ElementNotFoundException {
        // TODO: Implementar una vez se defina el flujo de autenticación.
        throw new UnsupportedOperationException("Funcionalidad pendiente de implementación.");
    }

    /**
     * Restablece la contraseña mediante código de verificación.
     * TODO: Implementar validación de código y actualización segura.
     */
    @Override
    public void resetPassword(ResetPasswordDto dto) throws InvalidElementException, ElementNotFoundException {
        // TODO: Implementar una vez se integre ResetCodeServiceImpl y EmailServiceImpl
        throw new UnsupportedOperationException("Funcionalidad pendiente de implementación.");
    }
}
