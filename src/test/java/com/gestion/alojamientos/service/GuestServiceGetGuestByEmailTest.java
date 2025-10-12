package com.gestion.alojamientos.service;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.mapper.users.GuestMapper;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.GuestServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class GuestServiceGetGuestByEmailTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestMapper guestMapper;

    @InjectMocks
    private GuestServiceImpl guestService;

    private Guest guest;
    private GuestDto guestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        guest = new Guest();
        guest.setId(1L);
        guest.setEmail("guest@example.com");
        guest.setName("Juan Pérez");
        guest.setUsername("juan123");
        guest.setPhoneNumber("3001234567");
        guest.setBirthDate(LocalDate.of(1995, 5, 12));
        guest.setState(StatesOfGuest.ACTIVE);
        guest.setRole(Role.GUEST);

        guestDto = new GuestDto(
                1L,
                "guest@example.com",
                "juan123",
                "Juan Pérez",
                "3001234567",
                LocalDate.of(1995, 5, 12),
                null,
                StatesOfGuest.ACTIVE,
                Role.GUEST,
                null,
                null,
                null
        );
    }

    // 1️⃣ Caso exitoso: huésped encontrado por email
    @Test
    void getGuestByEmail_ShouldReturnGuestDto_WhenGuestExists() {
        when(guestRepository.findByEmail("guest@example.com")).thenReturn(Optional.of(guest));
        when(guestMapper.toDto(guest)).thenReturn(guestDto);

        GuestDto result = guestService.getGuestByEmail("guest@example.com");

        assertNotNull(result);
        assertEquals(guestDto.email(), result.email());
        assertEquals("Juan Pérez", result.name());
        verify(guestRepository, times(1)).findByEmail("guest@example.com");
        verify(guestMapper, times(1)).toDto(guest);
    }

    // 2️⃣ Caso no encontrado: lanza EntityNotFoundException
    @Test
    void getGuestByEmail_ShouldThrowEntityNotFoundException_WhenGuestDoesNotExist() {
        when(guestRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> guestService.getGuestByEmail("noexiste@example.com")
        );

        assertEquals("Huésped no encontrado con email: noexiste@example.com", exception.getMessage());
        verify(guestRepository, times(1)).findByEmail("noexiste@example.com");
        verify(guestMapper, never()).toDto(any());
    }


}

