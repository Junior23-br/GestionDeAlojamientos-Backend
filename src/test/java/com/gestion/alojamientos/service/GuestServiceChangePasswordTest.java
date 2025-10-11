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

import com.gestion.alojamientos.dto.password.ChangePasswordDto;
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
 * Test class for GuestService changePassword method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class GuestServiceChangePasswordTest {

    @Mock
    private GuestRepository guestRepository;

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

    private ChangePasswordDto validChangePasswordDto;
    private Guest existingGuest;
    private final Long GUEST_ID = 1L;
    private final Long NON_EXISTENT_ID = 999L;
    private final String CORRECT_CURRENT_PASSWORD = "CurrentPass123";
    private final String INCORRECT_CURRENT_PASSWORD = "WrongPass456";
    private final String VALID_NEW_PASSWORD = "NewPassword123";
    private final String INVALID_NEW_PASSWORD = "weak";
    private final String ENCRYPTED_CURRENT_PASSWORD = "$2a$10$encryptedCurrentPassword";
    private final String ENCRYPTED_NEW_PASSWORD = "$2a$10$encryptedNewPassword";

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
        existingGuest.setPassword(ENCRYPTED_CURRENT_PASSWORD);

        // Setup valid change password DTO
        validChangePasswordDto = new ChangePasswordDto(
                CORRECT_CURRENT_PASSWORD,
                VALID_NEW_PASSWORD
        );
    }

    /**
     * Test 1: Éxito - Cambio exitoso de contraseña con datos válidos
     */
    @Test
    void changePassword_Success_ShouldChangePasswordAndSaveGuest() throws ElementNotFoundException, InvalidElementException {
        // Arrange
        Guest updatedGuest = new Guest();
        updatedGuest.setId(GUEST_ID);
        updatedGuest.setName("Juan Pérez");
        updatedGuest.setEmail("juan.perez@email.com");
        updatedGuest.setPhoneNumber("+573001234567");
        updatedGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        updatedGuest.setRole(Role.GUEST);
        updatedGuest.setState(StatesOfGuest.ACTIVE);
        updatedGuest.setPassword(ENCRYPTED_NEW_PASSWORD);

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(VALID_NEW_PASSWORD)).thenReturn(ENCRYPTED_NEW_PASSWORD);
        when(guestRepository.save(any(Guest.class))).thenReturn(updatedGuest);

        // Act
        guestService.changePassword(GUEST_ID, validChangePasswordDto);

        // Assert
        assertEquals(ENCRYPTED_NEW_PASSWORD, existingGuest.getPassword());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder).encode(VALID_NEW_PASSWORD);
        verify(guestRepository).save(existingGuest);
    }

    /**
     * Test 2: Fracaso - Usuario no encontrado por ID
     */
    @Test
    void changePassword_UserNotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(guestRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.changePassword(NON_EXISTENT_ID, validChangePasswordDto)
        );

        assertEquals("Usuario no encontrado con ID: " + NON_EXISTENT_ID, exception.getMessage());
        verify(guestRepository).findById(NON_EXISTENT_ID);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 3: Datos inválidos - Contraseña actual incorrecta
     */
    @Test
    void changePassword_IncorrectCurrentPassword_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ChangePasswordDto incorrectCurrentPasswordDto = new ChangePasswordDto(
                INCORRECT_CURRENT_PASSWORD,
                VALID_NEW_PASSWORD
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(INCORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(false);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.changePassword(GUEST_ID, incorrectCurrentPasswordDto)
        );

        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(INCORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 4: Caso edge - Nueva contraseña que no cumple con la política de seguridad
     */
    @Test
    void changePassword_InvalidNewPassword_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ChangePasswordDto invalidNewPasswordDto = new ChangePasswordDto(
                CORRECT_CURRENT_PASSWORD,
                INVALID_NEW_PASSWORD // Password doesn't meet policy
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.changePassword(GUEST_ID, invalidNewPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple las politicas de privacidad", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 5: Caso edge - Contraseña nueva con solo números
     */
    @Test
    void changePassword_NewPasswordWithOnlyNumbers_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ChangePasswordDto numbersOnlyPasswordDto = new ChangePasswordDto(
                CORRECT_CURRENT_PASSWORD,
                "12345678" // Only numbers, no letters
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.changePassword(GUEST_ID, numbersOnlyPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple las politicas de privacidad", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 6: Caso edge - Contraseña nueva demasiado corta
     */
    @Test
    void changePassword_NewPasswordTooShort_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ChangePasswordDto shortPasswordDto = new ChangePasswordDto(
                CORRECT_CURRENT_PASSWORD,
                "Ab1" // Too short, less than 8 characters
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.changePassword(GUEST_ID, shortPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple las politicas de privacidad", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 7: Caso edge - Contraseña nueva sin mayúsculas
     */
    @Test
    void changePassword_NewPasswordWithoutUppercase_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ChangePasswordDto noUppercasePasswordDto = new ChangePasswordDto(
                CORRECT_CURRENT_PASSWORD,
                "lowercase123" // No uppercase letters
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.changePassword(GUEST_ID, noUppercasePasswordDto)
        );

        assertEquals("La nueva contraseña no cumple las politicas de privacidad", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 8: Caso edge - Contraseña nueva sin números
     */
    @Test
    void changePassword_NewPasswordWithoutNumbers_ShouldThrowInvalidElementException() throws ElementNotFoundException {
        // Arrange
        ChangePasswordDto noNumbersPasswordDto = new ChangePasswordDto(
                CORRECT_CURRENT_PASSWORD,
                "NoNumbersHere" // No numbers
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.changePassword(GUEST_ID, noNumbersPasswordDto)
        );

        assertEquals("La nueva contraseña no cumple las politicas de privacidad", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 9: Caso edge - Cambio exitoso para huésped con estado SUSPENDED
     */
    @Test
    void changePassword_SuspendedGuest_ShouldAllowPasswordChange() throws ElementNotFoundException, InvalidElementException {
        // Arrange
        existingGuest.setState(StatesOfGuest.SUSPENDED);
        
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(VALID_NEW_PASSWORD)).thenReturn(ENCRYPTED_NEW_PASSWORD);
        when(guestRepository.save(any(Guest.class))).thenReturn(existingGuest);

        // Act
        guestService.changePassword(GUEST_ID, validChangePasswordDto);

        // Assert
        assertEquals(ENCRYPTED_NEW_PASSWORD, existingGuest.getPassword());
        assertEquals(StatesOfGuest.SUSPENDED, existingGuest.getState());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder).encode(VALID_NEW_PASSWORD);
        verify(guestRepository).save(existingGuest);
    }

    /**
     * Test 10: Caso edge - Cambio exitoso para huésped con estado INACTIVE
     */
    @Test
    void changePassword_InactiveGuest_ShouldAllowPasswordChange() throws ElementNotFoundException, InvalidElementException {
        // Arrange
        existingGuest.setState(StatesOfGuest.INACTIVE);
        
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(VALID_NEW_PASSWORD)).thenReturn(ENCRYPTED_NEW_PASSWORD);
        when(guestRepository.save(any(Guest.class))).thenReturn(existingGuest);

        // Act
        guestService.changePassword(GUEST_ID, validChangePasswordDto);

        // Assert
        assertEquals(ENCRYPTED_NEW_PASSWORD, existingGuest.getPassword());
        assertEquals(StatesOfGuest.INACTIVE, existingGuest.getState());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder).encode(VALID_NEW_PASSWORD);
        verify(guestRepository).save(existingGuest);
    }

    /**
     * Test 11: Caso edge - Cambio exitoso para huésped con rol HOST
     */
    @Test
    void changePassword_GuestWithHostRole_ShouldAllowPasswordChange() throws ElementNotFoundException, InvalidElementException {
        // Arrange
        existingGuest.setRole(Role.HOST);
        
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(VALID_NEW_PASSWORD)).thenReturn(ENCRYPTED_NEW_PASSWORD);
        when(guestRepository.save(any(Guest.class))).thenReturn(existingGuest);

        // Act
        guestService.changePassword(GUEST_ID, validChangePasswordDto);

        // Assert
        assertEquals(ENCRYPTED_NEW_PASSWORD, existingGuest.getPassword());
        assertEquals(Role.HOST, existingGuest.getRole());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(passwordEncoder).matches(CORRECT_CURRENT_PASSWORD, existingGuest.getPassword());
        verify(passwordEncoder).encode(VALID_NEW_PASSWORD);
        verify(guestRepository).save(existingGuest);
    }

    /**
     * Test 12: Caso edge - ID negativo
     */
    @Test
    void changePassword_NegativeId_ShouldThrowElementNotFoundException() {
        // Arrange
        Long negativeId = -1L;
        when(guestRepository.findById(negativeId)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.changePassword(negativeId, validChangePasswordDto)
        );

        assertEquals("Usuario no encontrado con ID: " + negativeId, exception.getMessage());
        verify(guestRepository).findById(negativeId);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }
}
