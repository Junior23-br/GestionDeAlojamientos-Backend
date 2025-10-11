package com.gestion.alojamientos.service.Impl;

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
import com.gestion.alojamientos.mapper.users.HostMapper;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Guest;
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
    
    @Override
    public HostDTO loginHost(UserLoginDTO dto) throws ElementNotFoundException, InvalidElementException {
        Host host = hostRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ElementNotFoundException("Host no encontrado con email: " + dto.email()));
        if (!passwordEncoder.matches(dto.password(), host.getPassword())) {
            throw new InvalidElementException("Contraseña incorrecta.");
        }
        if (host.getStatus() == StatesOfHost.DELETED) {
            throw new ElementNotFoundException("El perfil ha sido eliminado.");
        }
        return hostMapper.toDTO(host);
    }

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
     */
    @Override
    public String generateResetCode(String email) throws ElementNotFoundException {
        Host host = hostRepository.findByEmail(email)
                .orElseThrow(() -> new ElementNotFoundException("Huésped no encontrado con email: " + email));
        return resetCodeServiceImpl.generateAndSendCode(host);
    }

    /**
     * Cambia la contraseña de un Host.
     */
    @Override
    public void changePassword(Long userId, ChangePasswordDto dto) throws InvalidElementException, ElementNotFoundException {
        Host host = hostRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Usuario no encontrado con ID: " + userId));
        if(!passwordEncoder.matches(dto.currentPassword(), host.getPassword())) {
            throw  new InvalidElementException("Contraseña incorrecta");
        }
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if(!dto.newPassword().matches(passwordPattern)) {
            throw new InvalidElementException("La nueva contraseña no cumple las politicas de privacidad");
        }
        host.setPassword(passwordEncoder.encode(dto.newPassword()));
        hostRepository.save(host);
    }

    /**
     * Restablece la contraseña mediante código de verificación.
     * TODO: Implementar validación de código y actualización segura.
     */
    @Override
    public void resetPassword(ResetPasswordDto dto) throws InvalidElementException, ElementNotFoundException {
        Host host = hostRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ElementNotFoundException("Huésped no encontrado con email: " + dto.email()));
        //validar codigo
        try {
            // Validar código
            resetCodeServiceImpl.validateCode(host, dto.resetCode());
        } catch (InvalidElementException e) {
            if (e.getMessage().equals("Código de recuperación expirado.")) {
                // Generar y enviar un nuevo código automáticamente
                String newCode = resetCodeServiceImpl.generateAndSendCode(host);
                throw new InvalidElementException("El código ha expirado. Se ha enviado un nuevo código a " + dto.email() + ". Por favor, usa el nuevo código para restablecer tu contraseña.");
            }
            // Si es otro error (ej. código incorrecto o no generado), relanzar la excepción original
            throw e;
        }
        //validar politica calve
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!dto.newPassword().matches(passwordPattern)) {
            throw new InvalidElementException("La nueva contraseña no cumple con la política de seguridad.");
        }
        host.setPassword(passwordEncoder.encode(dto.newPassword()));
        // Limpiar el resetCode para que no pueda reutilizarse
        host.setResetCode(null); //limpiar
        hostRepository.save(host);
    }
}
