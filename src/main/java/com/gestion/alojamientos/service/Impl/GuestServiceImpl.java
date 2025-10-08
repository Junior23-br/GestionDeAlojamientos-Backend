package com.gestion.alojamientos.service.Impl;

import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.DeleteGuestDto;
import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.mapper.users.GuestMapper;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
@Service

public class GuestServiceImpl implements GuestService {
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private GuestMapper guestMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailServiceImpl emailServiceImpl;
    @Autowired
    private ResetCodeServiceImpl resetCodeServiceImpl;

    @Override
    /**
     * @param dto DTO con los datos del huesped a registrar
     * @return DTO del huesped creadp
     * @throws RepeatedElementException Si el correo o numero telefonico ya estan eegistrados
     * @throws InvalidElementException Si son datos que no cumplen con las validaciones
     */
    public GuestDto registerGuest(CreateGuestDto dto) throws RepeatedElementException, InvalidElementException {
        if (guestRepository.existsByEmail(dto.email())) {
            throw new RepeatedElementException("El correo electronico ya esta registrado");
        }
        if (guestRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new RepeatedElementException("El teléfono ya está registrado");
        }
        if (dto.birthDate().isAfter(LocalDate.now())) {
            throw new InvalidElementException("Fecha nacimiento es invalida");
        }
        if (!isOfAge(dto.birthDate())) {
            throw new InvalidElementException("Eres menor de edad, lo lamentamos ");
        }

        Guest guest = guestMapper.toEntity(dto);
        guest.setPassword(passwordEncoder.encode(dto.password())); // Encripta la contraseña
        guestRepository.save(guest);
        return guestMapper.toDto(guest);
    }

    /**
     * @param id  Identificador único del huesped.
     * @param dto DTO con los datos a actualizar.
     * @return DTO del huesped actualizado.
     * @throws ElementNotFoundException Si el huesped no existe.
     */
    @Override
    public GuestDto editGuest(Long id, EditGuestDto dto) throws ElementNotFoundException {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Usuario no encontrado con ID: " + id));
        //Validaer estado del huesped
        if (guest.getState() == StatesOfGuest.DELETED) {
            throw new ElementNotFoundException("El perfil ha sido eliminado y no puede editar la informacion");
        }
        if (!guest.getPhoneNumber().equals(dto.phoneNumber()) && guestRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new InvalidElementException("El numero de telefono ya esta registrado");
        }
        guestMapper.updateFromDto(dto, guest);
        return guestMapper.toDto(guestRepository.save(guest));
    }

    /**
     * Elimina lógicamente un huesped del sistema.
     *
     * @param id  Identificador único del huesped.
     * @param dto DTO con la contraseña para validar la eliminación.
     * @throws ElementNotFoundException Si el huesped no existe.
     * @throws InvalidElementException  Si la contraseña no coincide.
     */
    @Override
    public void deleteGuest(Long id, DeleteGuestDto dto) throws ElementNotFoundException, InvalidElementException {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Usuario no encontrado con ID: " + id));
        if (!passwordEncoder.matches(dto.password(), guest.getPassword())) {
            throw new InvalidElementException("Contraseña incorrecta");
        }
        guest.setState(StatesOfGuest.DELETED); // Cambiar el estado del huésped
        guestRepository.save(guest);
    }

    /**
     * @param id identificacor del huesped
     * @return dto del usuario obtenido
     * @throws ElementNotFoundException si el huesped no xiste
     */
    @Override
    public GuestDto getGuestById(Long id) throws ElementNotFoundException {
        return guestMapper.toDto(guestRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Huésped no encontrado con ID: " + id)));
    }

    /**
     * @param guest verificar que el huesped sea mayor de edad
     * @return verdadero o falso dependiendo si tiene la idea minima requerida
     */
    @Override
    public boolean isOfAge(LocalDate guest) {
        LocalDate today = LocalDate.now();
        Period period = Period.between(guest, today);
        return period.getYears() >= 18;
    }

    //Codigo aleatorio, busca al guest,
    // genera el código, lo asigna al guest y lo envía por email.
    @Override
    public String generateResetCode(String email) throws ElementNotFoundException {
        Guest guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new ElementNotFoundException("Huésped no encontrado con email: " + email));
        return resetCodeServiceImpl.generateAndSendCode(guest);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDto dto) throws InvalidElementException, ElementNotFoundException {

    }

    //Verificar que el codigo exista, actualiza la contraseña, verifica expiracion codigo
    @Override
    public void resetPassword(ResetPasswordDto dto) throws InvalidElementException, ElementNotFoundException {
        Guest guest = guestRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ElementNotFoundException("Huésped no encontrado con email: " + dto.email()));
        //validar codigo
        resetCodeServiceImpl.validateCode(guest,dto.resetCode());
        //validar politica calve
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!dto.newPassword().matches(passwordPattern)) {
            throw new InvalidElementException("La nueva contraseña no cumple con la política de seguridad.");
        }
        guest.setPassword(passwordEncoder.encode(dto.newPassword()));
        // Limpiar el resetCode para que no pueda reutilizarse
        guest.setResetCode(null); //limpiar
        guestRepository.save(guest);
    }
}



