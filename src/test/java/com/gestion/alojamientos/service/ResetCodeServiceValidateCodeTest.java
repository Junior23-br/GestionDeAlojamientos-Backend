package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.common.ResetCode;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.ResetCodeServiceImpl;

@ExtendWith(MockitoExtension.class)
class ResetCodeServiceValidateCodeTest {

    @Mock
    private EmailService emailService;

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private ResetCodeServiceImpl resetCodeService;

    private Guest testGuest;
    private ResetCode validResetCode;

    @BeforeEach
    void setUp() {
        // Setup test guest
        testGuest = new Guest();
        testGuest.setId(1L);
        testGuest.setEmail("test@example.com");
        testGuest.setUsername("testuser");
        testGuest.setPassword("password123");

        // Setup valid reset code
        validResetCode = new ResetCode();
        validResetCode.setResetCode("ABC1234");
        validResetCode.setExpirationDate(LocalDateTime.now().plusMinutes(15));
        testGuest.setResetCode(validResetCode);
    }

    // Éxito: Validación exitosa de código
    @Test
    void validateCode_Success() throws InvalidElementException {
        // Arrange
        String validCode = "ABC1234";

        // Act & Assert
        assertDoesNotThrow(() -> resetCodeService.validateCode(testGuest, validCode));
    }

    // Fracaso: Código incorrecto
    @Test
    void validateCode_Failure_IncorrectCode() {
        // Arrange
        String incorrectCode = "XYZ9999";

        // Act & Assert
        InvalidElementException exception = assertThrows(InvalidElementException.class,
                () -> resetCodeService.validateCode(testGuest, incorrectCode));

        assertEquals("Código de recuperación incorrecto.", exception.getMessage());
    }

    // Datos inválidos: Código expirado
    @Test
    void validateCode_InvalidData_ExpiredCode() {
        // Arrange
        String validCode = "ABC1234";
        // Set expiration date to past
        validResetCode.setExpirationDate(LocalDateTime.now().minusMinutes(1));

        // Act & Assert
        InvalidElementException exception = assertThrows(InvalidElementException.class,
                () -> resetCodeService.validateCode(testGuest, validCode));

        assertEquals("Código de recuperación expirado.", exception.getMessage());
    }

    // Edge case: Usuario sin código de reset
    @Test
    void validateCode_EdgeCase_NoResetCode() {
        // Arrange
        testGuest.setResetCode(null);
        String validCode = "ABC1234";

        // Act & Assert
        InvalidElementException exception = assertThrows(InvalidElementException.class,
                () -> resetCodeService.validateCode(testGuest, validCode));

        assertEquals("No se ha generado un código de restablecimiento.", exception.getMessage());
    }
}
