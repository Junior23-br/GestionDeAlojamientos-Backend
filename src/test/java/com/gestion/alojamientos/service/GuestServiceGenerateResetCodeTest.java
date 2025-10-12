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

import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.CloudinaryServiceImpl;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;
import com.gestion.alojamientos.service.Impl.GuestServiceImpl;
import com.gestion.alojamientos.service.Impl.ResetCodeServiceImpl;

/**
 * Test class for GuestService generateResetCode method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class GuestServiceGenerateResetCodeTest {

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

    private Guest existingGuest;
    private final String VALID_EMAIL = "juan.perez@email.com";
    private final String NON_EXISTENT_EMAIL = "nonexistent@email.com";
    private final String INVALID_EMAIL_FORMAT = "invalid-email-format";
    private final String GENERATED_RESET_CODE = "ABC1234";
    private final String ANOTHER_RESET_CODE = "XYZ7890";

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
        existingGuest.setPassword("$2a$10$encryptedPassword");
        existingGuest.setResetCode(null); // Initially no reset code
    }

    /**
     * Test 1: Éxito - Generación exitosa de código de reseteo para email válido
     */
    @Test
    void generateResetCode_Success_ShouldReturnGeneratedResetCode() throws ElementNotFoundException {
        // Arrange
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn(GENERATED_RESET_CODE);

        // Act
        String result = guestService.generateResetCode(VALID_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(GENERATED_RESET_CODE, result);
        assertEquals(7, result.length()); // Reset code should be 7 characters
        
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).generateAndSendCode(existingGuest);
    }

    /**
     * Test 2: Fracaso - Email no encontrado en la base de datos
     */
    @Test
    void generateResetCode_EmailNotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(guestRepository.findByEmail(NON_EXISTENT_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.generateResetCode(NON_EXISTENT_EMAIL)
        );

        assertEquals("Huésped no encontrado con email: " + NON_EXISTENT_EMAIL, exception.getMessage());
        verify(guestRepository).findByEmail(NON_EXISTENT_EMAIL);
        verify(resetCodeService, never()).generateAndSendCode(any(Guest.class));
    }

    /**
     * Test 3: Datos inválidos - Email con formato inválido
     */
    @Test
    void generateResetCode_InvalidEmailFormat_ShouldThrowElementNotFoundException() {
        // Arrange
        when(guestRepository.findByEmail(INVALID_EMAIL_FORMAT)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.generateResetCode(INVALID_EMAIL_FORMAT)
        );

        assertEquals("Huésped no encontrado con email: " + INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(guestRepository).findByEmail(INVALID_EMAIL_FORMAT);
        verify(resetCodeService, never()).generateAndSendCode(any(Guest.class));
    }

    /**
     * Test 4: Caso edge - Generación exitosa para huésped con estado DELETED
     */
    @Test
    void generateResetCode_DeletedGuest_ShouldStillGenerateResetCode() throws ElementNotFoundException {
        // Arrange
        existingGuest.setState(StatesOfGuest.DELETED);
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn(GENERATED_RESET_CODE);

        // Act
        String result = guestService.generateResetCode(VALID_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(GENERATED_RESET_CODE, result);
        assertEquals(StatesOfGuest.DELETED, existingGuest.getState());
        
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).generateAndSendCode(existingGuest);
    }

    /**
     * Test 5: Caso edge - Generación exitosa para huésped con estado SUSPENDED
     */
    @Test
    void generateResetCode_SuspendedGuest_ShouldStillGenerateResetCode() throws ElementNotFoundException {
        // Arrange
        existingGuest.setState(StatesOfGuest.SUSPENDED);
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn(GENERATED_RESET_CODE);

        // Act
        String result = guestService.generateResetCode(VALID_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(GENERATED_RESET_CODE, result);
        assertEquals(StatesOfGuest.SUSPENDED, existingGuest.getState());
        
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).generateAndSendCode(existingGuest);
    }

    /**
     * Test 6: Caso edge - Generación exitosa para huésped con estado INACTIVE
     */
    @Test
    void generateResetCode_InactiveGuest_ShouldStillGenerateResetCode() throws ElementNotFoundException {
        // Arrange
        existingGuest.setState(StatesOfGuest.INACTIVE);
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn(GENERATED_RESET_CODE);

        // Act
        String result = guestService.generateResetCode(VALID_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(GENERATED_RESET_CODE, result);
        assertEquals(StatesOfGuest.INACTIVE, existingGuest.getState());
        
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).generateAndSendCode(existingGuest);
    }

    /**
     * Test 7: Caso edge - Generación exitosa para huésped con rol HOST
     */
    @Test
    void generateResetCode_GuestWithHostRole_ShouldStillGenerateResetCode() throws ElementNotFoundException {
        // Arrange
        existingGuest.setRole(Role.HOST);
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn(GENERATED_RESET_CODE);

        // Act
        String result = guestService.generateResetCode(VALID_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(GENERATED_RESET_CODE, result);
        assertEquals(Role.HOST, existingGuest.getRole());
        
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).generateAndSendCode(existingGuest);
    }

    /**
     * Test 8: Caso edge - Email en mayúsculas (debería funcionar igual)
     */
    @Test
    void generateResetCode_UpperCaseEmail_ShouldGenerateResetCode() throws ElementNotFoundException {
        // Arrange
        String upperCaseEmail = "JUAN.PEREZ@EMAIL.COM";
        when(guestRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.of(existingGuest));
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn(GENERATED_RESET_CODE);

        // Act
        String result = guestService.generateResetCode(upperCaseEmail);

        // Assert
        assertNotNull(result);
        assertEquals(GENERATED_RESET_CODE, result);
        
        verify(guestRepository).findByEmail(upperCaseEmail);
        verify(resetCodeService).generateAndSendCode(existingGuest);
    }

    /**
     * Test 9: Caso edge - Email con espacios al inicio y final
     */
    @Test
    void generateResetCode_EmailWithSpaces_ShouldThrowElementNotFoundException() {
        // Arrange
        String emailWithSpaces = "  juan.perez@email.com  ";
        when(guestRepository.findByEmail(emailWithSpaces)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.generateResetCode(emailWithSpaces)
        );

        assertEquals("Huésped no encontrado con email: " + emailWithSpaces, exception.getMessage());
        verify(guestRepository).findByEmail(emailWithSpaces);
        verify(resetCodeService, never()).generateAndSendCode(any(Guest.class));
    }

    /**
     * Test 10: Caso edge - Email vacío
     */
    @Test
    void generateResetCode_EmptyEmail_ShouldThrowElementNotFoundException() {
        // Arrange
        String emptyEmail = "";
        when(guestRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.generateResetCode(emptyEmail)
        );

        assertEquals("Huésped no encontrado con email: " + emptyEmail, exception.getMessage());
        verify(guestRepository).findByEmail(emptyEmail);
        verify(resetCodeService, never()).generateAndSendCode(any(Guest.class));
    }

    /**
     * Test 11: Caso edge - Verificar que el código generado tiene el formato correcto
     */
    @Test
    void generateResetCode_ShouldReturnCodeWithCorrectFormat() throws ElementNotFoundException {
        // Arrange
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn(ANOTHER_RESET_CODE);

        // Act
        String result = guestService.generateResetCode(VALID_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(ANOTHER_RESET_CODE, result);
        assertEquals(7, result.length()); // Should be exactly 7 characters
        assertTrue(result.matches("[A-Z0-9]{7}")); // Should contain only uppercase letters and numbers
        
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).generateAndSendCode(existingGuest);
    }

    /**
     * Test 12: Caso edge - Verificar que se llama al servicio de email para enviar el código
     */
    @Test
    void generateResetCode_ShouldCallEmailService() throws ElementNotFoundException {
        // Arrange
        when(guestRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(existingGuest));
        when(resetCodeService.generateAndSendCode(existingGuest)).thenReturn(GENERATED_RESET_CODE);

        // Act
        String result = guestService.generateResetCode(VALID_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(GENERATED_RESET_CODE, result);
        
        // Verify that the ResetCodeService is called, which internally calls EmailService
        verify(guestRepository).findByEmail(VALID_EMAIL);
        verify(resetCodeService).generateAndSendCode(existingGuest);
    }
}
