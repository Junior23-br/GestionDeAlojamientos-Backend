package com.gestion.alojamientos.service;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.mapper.users.GuestMapper;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for AdminServiceImpl.changeGuestStatus method.
 * Covers: success, not found, null dependencies, and mapper error.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceChangeGuestStatusTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestMapper guestMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Guest existingGuest;
    private GuestDto expectedDto;

    @BeforeEach
    void setUp() {
        existingGuest = new Guest();
        existingGuest.setId(1L);
        existingGuest.setEmail("guest@mail.com");
        existingGuest.setState(StatesOfGuest.ACTIVE);

        expectedDto = new GuestDto(
                existingGuest.getId(),
                existingGuest.getEmail(),
                "GuestUser",
                null, null, null, null,
                StatesOfGuest.SUSPENDED,
                null,null,null,null
        );
    }

    /**
     * Test 1: Éxito - El huésped existe y se actualiza su estado correctamente.
     */
    @Test
    void changeGuestStatus_Success_ShouldUpdateStateAndReturnDto() {
        // Arrange
        when(guestRepository.findById(1L)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.save(existingGuest)).thenReturn(existingGuest);
        when(guestMapper.toDto(existingGuest)).thenReturn(expectedDto);

        // Act
        GuestDto result = adminService.changeGuestStatus(1L, StatesOfGuest.SUSPENDED);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.email(), result.email());
        assertEquals(StatesOfGuest.SUSPENDED, result.state());
        verify(guestRepository).findById(1L);
        verify(guestRepository).save(existingGuest);
        verify(guestMapper).toDto(existingGuest);
    }

    /**
     * Test 2: Fracaso - Huésped no encontrado
     */
    @Test
    void changeGuestStatus_GuestNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(guestRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.changeGuestStatus(99L, StatesOfGuest.SUSPENDED)
        );

        assertEquals("Huésped no encontrado con ID: 99", exception.getMessage());
        verify(guestRepository).findById(99L);
        verify(guestMapper, never()).toDto(any());
    }

    /**
     * Test 3: Dependencias nulas - guestRepository o guestMapper no disponibles
     */
    @Test
    void changeGuestStatus_NullDependencies_ShouldThrowUnsupportedOperationException() {
        // Creamos una nueva instancia del servicio sin mocks
        AdminServiceImpl serviceWithNulls = new AdminServiceImpl();

        // Act & Assert
        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> serviceWithNulls.changeGuestStatus(1L, StatesOfGuest.SUSPENDED)
        );

        assertTrue(ex.getMessage().contains("GuestRepository o GuestMapper no están disponibles"));
    }

    /**
     * Test 4: Error interno - Mapper lanza excepción
     */
    @Test
    void changeGuestStatus_MapperThrowsException_ShouldPropagateException() {
        // Arrange
        when(guestRepository.findById(1L)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.save(existingGuest)).thenReturn(existingGuest);
        when(guestMapper.toDto(existingGuest)).thenThrow(new RuntimeException("Error en el mapper"));

        // Act & Assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> adminService.changeGuestStatus(1L, StatesOfGuest.SUSPENDED)
        );

        assertTrue(ex.getMessage().contains("Error en el mapper"));
        verify(guestRepository).findById(1L);
        verify(guestMapper).toDto(existingGuest);
    }
}
