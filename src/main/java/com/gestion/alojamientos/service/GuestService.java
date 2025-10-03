package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.DeleteGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

/**
 * Firmas de metodos para implementar la logica
 */

@Service
public interface GuestService {

    GuestDto registerGuest(CreateGuestDto createGuestDto) throws  RepeatedElementException, InvalidElementException;
    GuestDto editGuest(Long id, EditGuestDto editGuestDto) throws ElementNotFoundException;
    void deleteGuest(Long id, DeleteGuestDto dto) throws ElementNotFoundException, InvalidElementException;
    GuestDto getGuestById(Long id) throws ElementNotFoundException;
    boolean isOfAge(LocalDate guest);
}

