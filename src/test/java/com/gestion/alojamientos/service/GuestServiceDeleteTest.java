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

import com.gestion.alojamientos.dto.guest.DeleteGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
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
 * Test class for GuestService deleteGuest method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class GuestServiceDeleteTest {

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

    private DeleteGuestDto validDeleteGuestDto;
    private Guest existingGuest;
    private GuestDto guestDto;
    private final Long GUEST_ID = 1L;
    private final String CORRECT_PASSWORD = "Password123";
    private final String INCORRECT_PASSWORD = "WrongPassword456";

    @BeforeEach
    void setUp() {
        // Setup existing guest with encrypted password
        existingGuest = new Guest();
        existingGuest.setId(GUEST_ID);
        existingGuest.setName("Juan Pérez");
        existingGuest.setEmail("juan.perez@email.com");
        existingGuest.setPhoneNumber("+573001234567");
        existingGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        existingGuest.setRole(Role.GUEST);
        existingGuest.setState(StatesOfGuest.ACTIVE);
        existingGuest.setPassword("$2a$10$encryptedPasswordHash"); // Encrypted password

        // Setup valid delete DTO
        validDeleteGuestDto = new DeleteGuestDto(
                GUEST_ID,
                CORRECT_PASSWORD
        );

        // Setup guest DTO for email confirmation
        guestDto = new GuestDto(
                GUEST_ID,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                null,
                StatesOfGuest.DELETED, // State after deletion
                Role.GUEST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    /**
     * Test 1: Éxito - Eliminación exitosa de un huésped con contraseña correcta
     */
    @Test
    void deleteGuest_Success_ShouldDeleteGuestAndSendConfirmationEmail() throws ElementNotFoundException, InvalidElementException {
        // Arrange
        Guest deletedGuest = new Guest();
        deletedGuest.setId(GUEST_ID);
        deletedGuest.setName("Juan Pérez");
        deletedGuest.setEmail("juan.perez@email.com");
        deletedGuest.setPhoneNumber("+573001234567");
        deletedGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        deletedGuest.setRole(Role.GUEST);
        deletedGuest.setState(StatesOfGuest.DELETED); // State changed to DELETED
        deletedGuest.setPassword("$2a$10$encryptedPasswordHash");

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_PASSWORD, existingGuest.getPassword())).thenReturn(true);
        when(guestRepository.save(any(Guest.class))).thenReturn(deletedGuest);
        when(guestMapper.toDto(deletedGuest)).thenReturn(guestDto);
        doNothing().when(emailService).sendAccountDeletionConfirmationEmail(anyString(), any(GuestDto.class));

        // Act
        guestService.deleteGuest(GUEST_ID, validDeleteGuestDto);

        // Assert
        assertEquals(StatesOfGuest.DELETED, deletedGuest.getState());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_PASSWORD, existingGuest.getPassword());
        verify(guestRepository).save(existingGuest);
        verify(guestMapper).toDto(deletedGuest);
        verify(emailService).sendAccountDeletionConfirmationEmail("juan.perez@email.com", guestDto);
    }

    /**
     * Test 2: Fracaso - Huésped no encontrado
     */
    @Test
    void deleteGuest_GuestNotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.deleteGuest(GUEST_ID, validDeleteGuestDto)
        );

        assertEquals("Usuario no encontrado con ID: " + GUEST_ID, exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(guestRepository, never()).save(any(Guest.class));
        verify(emailService, never()).sendAccountDeletionConfirmationEmail(anyString(), any(GuestDto.class));
    }

    /**
     * Test 3: Datos inválidos - Contraseña incorrecta
     */
    @Test
    void deleteGuest_IncorrectPassword_ShouldThrowInvalidElementException() {
        // Arrange
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(INCORRECT_PASSWORD, existingGuest.getPassword())).thenReturn(false);

        DeleteGuestDto incorrectPasswordDto = new DeleteGuestDto(GUEST_ID, INCORRECT_PASSWORD);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.deleteGuest(GUEST_ID, incorrectPasswordDto)
        );

        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(INCORRECT_PASSWORD, existingGuest.getPassword());
        verify(guestRepository, never()).save(any(Guest.class));
        verify(emailService, never()).sendAccountDeletionConfirmationEmail(anyString(), any(GuestDto.class));
    }

    /**
     * Test 4: Caso edge - Intentar eliminar un huésped ya eliminado (estado DELETED)
     */
    @Test
    void deleteGuest_AlreadyDeletedGuest_ShouldStillAllowDeletionAndSendEmail() throws ElementNotFoundException, InvalidElementException {
        // Arrange
        existingGuest.setState(StatesOfGuest.DELETED); // Already deleted
        Guest alreadyDeletedGuest = new Guest();
        alreadyDeletedGuest.setId(GUEST_ID);
        alreadyDeletedGuest.setName("Juan Pérez");
        alreadyDeletedGuest.setEmail("juan.perez@email.com");
        alreadyDeletedGuest.setPhoneNumber("+573001234567");
        alreadyDeletedGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        alreadyDeletedGuest.setRole(Role.GUEST);
        alreadyDeletedGuest.setState(StatesOfGuest.DELETED);
        alreadyDeletedGuest.setPassword("$2a$10$encryptedPasswordHash");

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_PASSWORD, existingGuest.getPassword())).thenReturn(true);
        when(guestRepository.save(any(Guest.class))).thenReturn(alreadyDeletedGuest);
        when(guestMapper.toDto(alreadyDeletedGuest)).thenReturn(guestDto);
        doNothing().when(emailService).sendAccountDeletionConfirmationEmail(anyString(), any(GuestDto.class));

        // Act
        guestService.deleteGuest(GUEST_ID, validDeleteGuestDto);

        // Assert
        assertEquals(StatesOfGuest.DELETED, alreadyDeletedGuest.getState());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_PASSWORD, existingGuest.getPassword());
        verify(guestRepository).save(existingGuest);
        verify(guestMapper).toDto(alreadyDeletedGuest);
        verify(emailService).sendAccountDeletionConfirmationEmail("juan.perez@email.com", guestDto);
    }

    /**
     * Test 5: Caso edge - Eliminación exitosa de un huésped con estado SUSPENDED
     */
    @Test
    void deleteGuest_SuspendedGuest_ShouldAllowDeletionAndSendEmail() throws ElementNotFoundException, InvalidElementException {
        // Arrange
        existingGuest.setState(StatesOfGuest.SUSPENDED); // Suspended guest
        
        Guest suspendedGuest = new Guest();
        suspendedGuest.setId(GUEST_ID);
        suspendedGuest.setName("Juan Pérez");
        suspendedGuest.setEmail("juan.perez@email.com");
        suspendedGuest.setPhoneNumber("+573001234567");
        suspendedGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        suspendedGuest.setRole(Role.GUEST);
        suspendedGuest.setState(StatesOfGuest.DELETED); // State changed to DELETED
        suspendedGuest.setPassword("$2a$10$encryptedPasswordHash");

        GuestDto suspendedGuestDto = new GuestDto(
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
        when(passwordEncoder.matches(CORRECT_PASSWORD, existingGuest.getPassword())).thenReturn(true);
        when(guestRepository.save(any(Guest.class))).thenReturn(suspendedGuest);
        when(guestMapper.toDto(suspendedGuest)).thenReturn(suspendedGuestDto);
        doNothing().when(emailService).sendAccountDeletionConfirmationEmail(anyString(), any(GuestDto.class));

        // Act
        guestService.deleteGuest(GUEST_ID, validDeleteGuestDto);

        // Assert
        assertEquals(StatesOfGuest.DELETED, suspendedGuest.getState());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_PASSWORD, existingGuest.getPassword());
        verify(guestRepository).save(existingGuest);
        verify(guestMapper).toDto(suspendedGuest);
        verify(emailService).sendAccountDeletionConfirmationEmail("juan.perez@email.com", suspendedGuestDto);
    }

    /**
     * Test 6: Caso edge - Eliminación con contraseña vacía o nula
     */
    @Test
    void deleteGuest_EmptyPassword_ShouldThrowInvalidElementException() {
        // Arrange
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches("", existingGuest.getPassword())).thenReturn(false);

        DeleteGuestDto emptyPasswordDto = new DeleteGuestDto(GUEST_ID, "");

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.deleteGuest(GUEST_ID, emptyPasswordDto)
        );

        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches("", existingGuest.getPassword());
        verify(guestRepository, never()).save(any(Guest.class));
        verify(emailService, never()).sendAccountDeletionConfirmationEmail(anyString(), any(GuestDto.class));
    }

    /**
     * Test 7: Caso edge - Eliminación con ID inválido (negativo)
     */
    @Test
    void deleteGuest_InvalidId_ShouldThrowElementNotFoundException() {
        // Arrange
        Long invalidId = -1L;
        when(guestRepository.findById(invalidId)).thenReturn(Optional.empty());

        DeleteGuestDto invalidIdDto = new DeleteGuestDto(invalidId, CORRECT_PASSWORD);

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.deleteGuest(invalidId, invalidIdDto)
        );

        assertEquals("Usuario no encontrado con ID: " + invalidId, exception.getMessage());
        verify(guestRepository).findById(invalidId);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(guestRepository, never()).save(any(Guest.class));
        verify(emailService, never()).sendAccountDeletionConfirmationEmail(anyString(), any(GuestDto.class));
    }
}
