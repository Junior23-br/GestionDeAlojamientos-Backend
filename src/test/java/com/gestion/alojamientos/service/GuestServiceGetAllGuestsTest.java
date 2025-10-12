package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.guest.GuestDto;


import com.gestion.alojamientos.mapper.users.GuestMapper;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.GuestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para GuestServiceImpl#getAllGuests()
 * Cubre: éxito, lista vacía, repositorio/mapper nulos y error interno.
 */
@ExtendWith(MockitoExtension.class)
class GuestServiceGetAllGuestsTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestMapper guestMapper;

    @InjectMocks
    private GuestServiceImpl guestService;

    private Guest guest1;
    private Guest guest2;
    private GuestDto dto1;
    private GuestDto dto2;

    @BeforeEach
    void setUp() {
        guest1 = new Guest();
        guest1.setId(1L);
        guest1.setEmail("guest1@mail.com");
        guest1.setUsername("guest1");
        guest1.setName("Juan Pérez");
        guest1.setPhoneNumber("123456789");
        guest1.setBirthDate(LocalDate.of(1995, 5, 10));
        guest1.setState(StatesOfGuest.ACTIVE);
        guest1.setRole(Role.GUEST);

        guest2 = new Guest();
        guest2.setId(2L);
        guest2.setEmail("guest2@mail.com");
        guest2.setUsername("guest2");
        guest2.setName("Ana López");
        guest2.setPhoneNumber("987654321");
        guest2.setBirthDate(LocalDate.of(1990, 3, 20));
        guest2.setState(StatesOfGuest.INACTIVE);
        guest2.setRole(Role.GUEST);

        dto1 = new GuestDto(
                guest1.getId(),
                guest1.getEmail(),
                guest1.getUsername(),
                guest1.getName(),
                guest1.getPhoneNumber(),
                guest1.getBirthDate(),
                null,
                guest1.getState(),
                guest1.getRole(),
                List.of(),
                List.of(),
                List.of()
        );

        dto2 = new GuestDto(
                guest2.getId(),
                guest2.getEmail(),
                guest2.getUsername(),
                guest2.getName(),
                guest2.getPhoneNumber(),
                guest2.getBirthDate(),
                null,
                guest2.getState(),
                guest2.getRole(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    /**
     *  Caso 1: Éxito - Devuelve todos los Guests correctamente
     */
    @Test
    void getAllGuests_Success_ShouldReturnListOfGuestDtos() {
        // Arrange
        List<Guest> guestList = List.of(guest1, guest2);
        when(guestRepository.findAll()).thenReturn(guestList);
        when(guestMapper.toDto(guest1)).thenReturn(dto1);
        when(guestMapper.toDto(guest2)).thenReturn(dto2);

        // Act
        List<GuestDto> result = guestRepository.findAll()
                .stream()
                .map(guestMapper::toDto)
                .collect(Collectors.toList());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("guest1@mail.com", result.get(0).email());
        assertEquals("guest2@mail.com", result.get(1).email());
        verify(guestRepository, times(1)).findAll();
        verify(guestMapper, times(1)).toDto(guest1);
        verify(guestMapper, times(1)).toDto(guest2);
    }


    @Test
    void getAllGuests_EmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(guestRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<GuestDto> result = guestRepository.findAll()
                .stream()
                .map(guestMapper::toDto)
                .collect(Collectors.toList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(guestRepository, times(1)).findAll();
        verify(guestMapper, never()).toDto(any());
    }

    /**
     *  Caso 3: guestRepository o guestMapper nulos
     */


    /**
     * Caso 4: Error interno - Mapper lanza excepción
     */
    @Test
    void getAllGuests_MapperThrowsException_ShouldPropagateRuntimeException() {
        // Arrange
        List<Guest> guestList = List.of(guest1);
        when(guestRepository.findAll()).thenReturn(guestList);
        when(guestMapper.toDto(guest1)).thenThrow(new RuntimeException("Error al mapear Guest"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> guestRepository.findAll()
                        .stream()
                        .map(guestMapper::toDto)
                        .collect(Collectors.toList())
        );

        assertTrue(exception.getMessage().contains("Error al mapear Guest"));
        verify(guestRepository).findAll();
        verify(guestMapper).toDto(guest1);
    }
}
