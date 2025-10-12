package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.mapper.users.GuestMapper;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.CloudinaryServiceImpl;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;
import com.gestion.alojamientos.service.Impl.GuestServiceImpl;
import com.gestion.alojamientos.service.Impl.ResetCodeServiceImpl;

/**
 * Test class for GuestService getGuestById method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class GuestServiceGetByIdTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestMapper guestMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private ResetCodeServiceImpl resetCodeService;

    @Mock
    private CloudinaryServiceImpl cloudinaryService;

    @InjectMocks
    private GuestServiceImpl guestService;

    private Guest existingGuest;
    private GuestDto expectedGuestDto;
    private final Long GUEST_ID = 1L;
    private final Long NON_EXISTENT_ID = 999L;

    @BeforeEach
    void setUp() {
        // Setup existing guest
        existingGuest = new Guest();
        existingGuest.setId(GUEST_ID);
        existingGuest.setName("Juan Pérez");
        existingGuest.setEmail("juan.perez@email.com");
        existingGuest.setPhoneNumber("+573001234567");
        existingGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        existingGuest.setRole(Role.GUEST);
        existingGuest.setState(StatesOfGuest.ACTIVE);
        existingGuest.setUrlProfilePhoto("https://cloudinary.com/profile-photo.jpg");

        // Setup expected guest DTO
        expectedGuestDto = new GuestDto(
                GUEST_ID,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                null,
                StatesOfGuest.ACTIVE,
                Role.GUEST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    /**
     * Test 1: Éxito - Obtención exitosa de un huésped por ID válido
     */
    @Test
    void getGuestById_Success_ShouldReturnGuestDto() throws ElementNotFoundException {
        // Arrange
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestMapper.toDto(existingGuest)).thenReturn(expectedGuestDto);

        // Act
        GuestDto result = guestService.getGuestById(GUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(expectedGuestDto.id(), result.id());
        assertEquals(expectedGuestDto.email(), result.email());
        assertEquals(expectedGuestDto.name(), result.name());
        assertEquals(expectedGuestDto.phoneNumber(), result.phoneNumber());
        assertEquals(expectedGuestDto.birthDate(), result.birthDate());
        assertEquals(expectedGuestDto.state(), result.state());
        assertEquals(expectedGuestDto.role(), result.role());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(guestMapper).toDto(existingGuest);
    }

    /**
     * Test 2: Fracaso - Huésped no encontrado con ID válido pero inexistente
     */
    @Test
    void getGuestById_GuestNotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(guestRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.getGuestById(NON_EXISTENT_ID)
        );

        assertEquals("Huésped no encontrado con ID: " + NON_EXISTENT_ID, exception.getMessage());
        verify(guestRepository).findById(NON_EXISTENT_ID);
        verify(guestMapper, never()).toDto(any(Guest.class));
    }

    /**
     * Test 3: Datos inválidos - ID negativo
     */
    @Test
    void getGuestById_NegativeId_ShouldThrowElementNotFoundException() {
        // Arrange
        Long negativeId = -1L;
        when(guestRepository.findById(negativeId)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.getGuestById(negativeId)
        );

        assertEquals("Huésped no encontrado con ID: " + negativeId, exception.getMessage());
        verify(guestRepository).findById(negativeId);
        verify(guestMapper, never()).toDto(any(Guest.class));
    }

    /**
     * Test 4: Caso edge - Obtención de huésped con estado DELETED
     */
    @Test
    void getGuestById_DeletedGuest_ShouldReturnGuestDto() throws ElementNotFoundException {
        // Arrange
        existingGuest.setState(StatesOfGuest.DELETED);
        
        GuestDto deletedGuestDto = new GuestDto(
                GUEST_ID,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                null,
                StatesOfGuest.DELETED,
                Role.GUEST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestMapper.toDto(existingGuest)).thenReturn(deletedGuestDto);

        // Act
        GuestDto result = guestService.getGuestById(GUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(GUEST_ID, result.id());
        assertEquals("juan.perez@email.com", result.email());
        assertEquals("Juan Pérez", result.name());
        assertEquals(StatesOfGuest.DELETED, result.state());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(guestMapper).toDto(existingGuest);
    }

    /**
     * Test 5: Caso edge - Obtención de huésped con estado SUSPENDED
     */
    @Test
    void getGuestById_SuspendedGuest_ShouldReturnGuestDto() throws ElementNotFoundException {
        // Arrange
        existingGuest.setState(StatesOfGuest.SUSPENDED);
        
        GuestDto suspendedGuestDto = new GuestDto(
                GUEST_ID,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                null,
                StatesOfGuest.SUSPENDED,
                Role.GUEST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestMapper.toDto(existingGuest)).thenReturn(suspendedGuestDto);

        // Act
        GuestDto result = guestService.getGuestById(GUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(GUEST_ID, result.id());
        assertEquals("juan.perez@email.com", result.email());
        assertEquals("Juan Pérez", result.name());
        assertEquals(StatesOfGuest.SUSPENDED, result.state());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(guestMapper).toDto(existingGuest);
    }

    /**
     * Test 6: Caso edge - Obtención de huésped con estado INACTIVE
     */
    @Test
    void getGuestById_InactiveGuest_ShouldReturnGuestDto() throws ElementNotFoundException {
        // Arrange
        existingGuest.setState(StatesOfGuest.INACTIVE);
        
        GuestDto inactiveGuestDto = new GuestDto(
                GUEST_ID,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                null,
                StatesOfGuest.INACTIVE,
                Role.GUEST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestMapper.toDto(existingGuest)).thenReturn(inactiveGuestDto);

        // Act
        GuestDto result = guestService.getGuestById(GUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(GUEST_ID, result.id());
        assertEquals("juan.perez@email.com", result.email());
        assertEquals("Juan Pérez", result.name());
        assertEquals(StatesOfGuest.INACTIVE, result.state());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(guestMapper).toDto(existingGuest);
    }

    /**
     * Test 7: Caso edge - Obtención de huésped con rol HOST
     */
    @Test
    void getGuestById_GuestWithHostRole_ShouldReturnGuestDto() throws ElementNotFoundException {
        // Arrange
        existingGuest.setRole(Role.HOST);
        
        GuestDto hostGuestDto = new GuestDto(
                GUEST_ID,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                null,
                StatesOfGuest.ACTIVE,
                Role.HOST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestMapper.toDto(existingGuest)).thenReturn(hostGuestDto);

        // Act
        GuestDto result = guestService.getGuestById(GUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(GUEST_ID, result.id());
        assertEquals("juan.perez@email.com", result.email());
        assertEquals("Juan Pérez", result.name());
        assertEquals(Role.HOST, result.role());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(guestMapper).toDto(existingGuest);
    }

    /**
     * Test 8: Caso edge - ID con valor cero
     */
    @Test
    void getGuestById_ZeroId_ShouldThrowElementNotFoundException() {
        // Arrange
        Long zeroId = 0L;
        when(guestRepository.findById(zeroId)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.getGuestById(zeroId)
        );

        assertEquals("Huésped no encontrado con ID: " + zeroId, exception.getMessage());
        verify(guestRepository).findById(zeroId);
        verify(guestMapper, never()).toDto(any(Guest.class));
    }
}
