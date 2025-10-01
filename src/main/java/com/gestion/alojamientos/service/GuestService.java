package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.DeleteGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.mapper.GuestMapper;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.Guest;
import com.gestion.alojamientos.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;

@Service
public class GuestService {
    /**
     * Servicio que define las operaciones principales para la gestion
     * Incluye metodos que permitirán registrar, editar, eliminar
     * Obtener HUESPEDES por firstName o correo electronico
     * Cada operacion lanzará excepciones especificas
     */
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private GuestMapper guestMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
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
        if (dto.birthDate().isAfter((LocalDate.now()))) {
            throw new InvalidElementException("Fecha nacimiento es invalida");
        }
        if(!isOfAge(dto.birthDate())) {
            throw new InvalidElementException("Eres menor de edad, lo lamentamos;(");
        }

        Guest guest = guestMapper.toEntity(dto);
        guest.setPassword(passwordEncoder.encode(dto.password())); // Encripta la contraseña
        guestRepository.save(guest);
        return guestMapper.toDto(guest);
    }
        /**
         *@param id  Identificador único del huesped.
         *@param dto DTO con los datos a actualizar.
         *@return DTO del huesped actualizado.
         *@throws ElementNotFoundException Si el huesped no existe.
         */
        public GuestDto editGuest(Long id, EditGuestDto dto) throws ElementNotFoundException {
            Guest guest = guestRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Usuario no encontrado con ID: " + id));
            guestMapper.updateFromDto(dto, guest);
            return guestMapper.toDto(guestRepository.save(guest));
        }
        /**
         * Elimina lógicamente un huesped del sistema.
         *
         * @param id  Identificador único del huesped.
         * @param dto DTO con la contraseña para validar la eliminación.
         * @throws ElementNotFoundException Si el huesped no existe.
         * @throws InvalidElementException Si la contraseña no coincide.
         */
        public void deleteGuest(Long id, DeleteGuestDto dto ) throws ElementNotFoundException, InvalidElementException {
            Guest guest = guestRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Usuario no encontrado con ID: " + id));
        if(!passwordEncoder.matches(dto.password(), guest.getPassword())) {
            throw new InvalidElementException("Contraseñia incorrecta");
        }
            guest.setState(StatesOfHost.DELETED); //cambiar el estado del huesped
            guestRepository.save(guest);
        }
    /**
     *
     * @param id identificacor del huesped
     * @return dto del usuario obtenido
     * @throws ElementNotFoundException si el huesped no xiste
     */
        public GuestDto getGuestById (Long id) throws ElementNotFoundException {
            return guestMapper.toDto(guestRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Huésped no encontrado con ID: " + id)));
        }
    /**
     * @param guest verificar que el huesped sea mayor de edad
     * @return verdadero o falso dependiendo si tiene la idea minima requerida
     */
    public boolean isOfAge(LocalDate guest) {
            LocalDate today = LocalDate.now();
            Period period = Period.between(guest, today);
            return period.getYears() >=18;
        }
}

