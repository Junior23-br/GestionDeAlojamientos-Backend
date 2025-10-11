package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.CloudinaryServiceImpl;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;
import com.gestion.alojamientos.service.Impl.GuestServiceImpl;
import com.gestion.alojamientos.service.Impl.ResetCodeServiceImpl;

/**
 * Test class for GuestService resetPassword method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class GuestServiceResetPasswordTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private ResetCodeServiceImpl resetCodeService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private CloudinaryServiceImpl cloudinaryService;

    @InjectMocks
    private GuestServiceImpl guestService;

    private ResetPasswordDto validResetPasswordDto;
    private Guest existingGuest;
    private final String VALID_EMAIL = "juan.perez@email.com";
    private final String VALID_RESET_CODE = "123456";
    private final String VALID_NEW_PASSWORD = "NewPassword123";
    private final String INVALID_RESET_CODE = "999999";
    private final String INVALID_PASSWORD = "weak";
    private final String ENCRYPTED_PASSWORD = "$2a$10$encryptedPasswordHash";

    @BeforeEach
    void setUp() {
        // Setup existing guest
        existingGuest = new Guest();
        existingGuest.setId(1L);
        existingGuest.setName("Juan Pérez");
        existingGuest.setEmail(VALID_EMAIL);
        existingGuest.setPhoneNumber("+573001234567");
        existingGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        existingGuest.setRole(Role.GUEST);
        existingGuest.setState(StatesOfGuest.ACTIVE);
        existingGuest.setPassword("$2a$10$oldEncryptedPassword");
        existingGuest.setResetCode(null); // Will be set in tests

        // Setup valid reset password DTO
        validResetPasswordDto = new ResetPasswordDto(
                VALID_EMAIL,
                VALID_RESET_CODE,
                VALID_NEW_PASSWORD
        );
    }

    /**
     * Test 1: Éxito - Reseteo exitoso de contraseña con datos válidos
     */
    @Test
    void resetPassword_Success_ShouldResetPasswordAndClearResetCode() throws ElementNotFoundException, InvalidElementException {
        // Arrange
        Guest updatedGuest = new Guest();
        updatedGuest.setId(1L);
        updatedGuest.setName("Juan Pérez");
        updatedGuest.setEmail(VALID_EMAIL);
        updatedGuest.setPhoneNumber("+573001234567");
        updatedGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        updatedGuest.setRole(Role.GUEST);
        updatedGuest.setState(StatesOfGuest.ACTIVE);
        updatedGuest.setPassword(ENCRYPTED_PASSWORD);
        updatedGuest.setResetCode(null); // Cleared after successful reset

        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        doNothing().when(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        when(passwordEncoder.encode(VALID_NEW_PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        when(guestRepository.save(any(Guest.class))).thenReturn(updatedGuest);

        // Act
        guestService.resetPassword(validResetPasswordDto);

        // Assert
        assertEquals(ENCRYPTED_PASSWORD, existingGuest.getPassword());
        assertNull(existingGuest.getResetCode()); // Reset code should be cleared
        
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        verify(passwordEncoder).encode(VALID_NEW_PASSWORD);
        verify(guestRepository).save(existingGuest);
    }

    /**
     * Test 2: Fracaso - Huésped no encontrado por email
     */
    @Test
    void resetPassword_GuestNotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.resetPassword(validResetPasswordDto)
        );

        assertEquals("Huésped no encontrado con email: " + VALID_EMAIL, exception.getMessage());
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService, never()).validateCode(any(Guest.class), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 3: Datos inválidos - Contraseña que no cumple con la política de seguridad
     */
    @Test
    void resetPassword_InvalidPassword_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ResetPasswordDto invalidPasswordDto = new ResetPasswordDto(
                VALID_EMAIL,
                VALID_RESET_CODE,
                INVALID_PASSWORD // Password doesn't meet policy
        );

        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        doNothing().when(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.resetPassword(invalidPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple con la política de seguridad.", exception.getMessage());
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 4: Caso edge - Código de reseteo expirado (debe generar nuevo código)
     */
    @Test
    void resetPassword_ExpiredResetCode_ShouldGenerateNewCodeAndThrowException() throws ElementNotFoundException {
        // Arrange
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        doThrow(new InvalidElementException("Código de recuperación expirado."))
                .when(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn("NEW123456");

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.resetPassword(validResetPasswordDto)
        );

        assertTrue(exception.getMessage().contains("El código ha expirado"));
        assertTrue(exception.getMessage().contains("Se ha enviado un nuevo código"));
        assertTrue(exception.getMessage().contains(VALID_EMAIL));
        
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        verify(resetCodeService).generateAndSendCode(existingGuest);
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 5: Caso edge - Código de reseteo incorrecto
     */
    @Test
    void resetPassword_IncorrectResetCode_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ResetPasswordDto incorrectCodeDto = new ResetPasswordDto(
                VALID_EMAIL,
                INVALID_RESET_CODE,
                VALID_NEW_PASSWORD
        );

        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        doThrow(new InvalidElementException("Código de recuperación incorrecto."))
                .when(resetCodeService).validateCode(existingGuest, INVALID_RESET_CODE);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.resetPassword(incorrectCodeDto)
        );

        assertEquals("Código de recuperación incorrecto.", exception.getMessage());
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).validateCode(existingGuest, INVALID_RESET_CODE);
        verify(resetCodeService, never()).generateAndSendCode(any(Guest.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 6: Caso edge - Email con formato inválido
     */
    @Test
    void resetPassword_InvalidEmailFormat_ShouldThrowElementNotFoundException() {
        // Arrange
        String invalidEmail = "invalid-email-format";
        ResetPasswordDto invalidEmailDto = new ResetPasswordDto(
                invalidEmail,
                VALID_RESET_CODE,
                VALID_NEW_PASSWORD
        );

        when(guestRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.resetPassword(invalidEmailDto)
        );

        assertEquals("Huésped no encontrado con email: " + invalidEmail, exception.getMessage());
        verify(guestRepository).findByEmail(invalidEmail);
        verify(resetCodeService, never()).validateCode(any(Guest.class), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 7: Caso edge - Contraseña con solo números
     */
    @Test
    void resetPassword_PasswordWithOnlyNumbers_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ResetPasswordDto numbersOnlyPasswordDto = new ResetPasswordDto(
                VALID_EMAIL,
                VALID_RESET_CODE,
                "12345678" // Only numbers, no letters
        );

        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        doNothing().when(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.resetPassword(numbersOnlyPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple con la política de seguridad.", exception.getMessage());
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 8: Caso edge - Contraseña demasiado corta
     */
    @Test
    void resetPassword_PasswordTooShort_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ResetPasswordDto shortPasswordDto = new ResetPasswordDto(
                VALID_EMAIL,
                VALID_RESET_CODE,
                "Ab1" // Too short, less than 8 characters
        );

        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        doNothing().when(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.resetPassword(shortPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple con la política de seguridad.", exception.getMessage());
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 9: Caso edge - Contraseña sin mayúsculas
     */
    @Test
    void resetPassword_PasswordWithoutUppercase_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ResetPasswordDto noUppercasePasswordDto = new ResetPasswordDto(
                VALID_EMAIL,
                VALID_RESET_CODE,
                "lowercase123" // No uppercase letters
        );

        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        doNothing().when(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.resetPassword(noUppercasePasswordDto)
        );

        assertEquals("La nueva contraseña no cumple con la política de seguridad.", exception.getMessage());
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 10: Caso edge - Contraseña sin números
     */
    @Test
    void resetPassword_PasswordWithoutNumbers_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ResetPasswordDto noNumbersPasswordDto = new ResetPasswordDto(
                VALID_EMAIL,
                VALID_RESET_CODE,
                "NoNumbersHere" // No numbers
        );

        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        doNothing().when(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.resetPassword(noNumbersPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple con la política de seguridad.", exception.getMessage());
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).validateCode(existingGuest, VALID_RESET_CODE);
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }
}
