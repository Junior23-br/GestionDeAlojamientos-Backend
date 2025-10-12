package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.service.Impl.HostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para el método isOfAge del servicio HostServiceImpl.
 * Prueba la funcionalidad de verificar si un host es mayor de edad.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceIsOfAgeTest {

    @InjectMocks
    private HostServiceImpl hostService;

    private LocalDate adultBirthDate;
    private LocalDate minorBirthDate;
    private LocalDate exactlyEighteenBirthDate;
    private LocalDate veryOldBirthDate;

    @BeforeEach
    void setUp() {
        adultBirthDate = LocalDate.now().minusYears(25);        // 25 years old
        minorBirthDate = LocalDate.now().minusYears(16);        // 16 years old
        exactlyEighteenBirthDate = LocalDate.now().minusYears(18); // Exactly 18 years old
        veryOldBirthDate = LocalDate.now().minusYears(80);      // 80 years old
    }

    /**
     * Prueba el caso de éxito: verificar que un adulto es mayor de edad.
     * Verifica que se retorne true cuando la persona tiene más de 18 años.
     */
    @Test
    void shouldReturnTrue_WhenPersonIsAdult() {
        // Given - adultBirthDate (25 years old)

        // When
        boolean result = hostService.isOfAge(adultBirthDate);

        // Then
        assertTrue(result);
    }

    /**
     * Prueba el caso de fracaso: verificar que un menor no es mayor de edad.
     * Verifica que se retorne false cuando la persona tiene menos de 18 años.
     */
    @Test
    void shouldReturnFalse_WhenPersonIsMinor() {
        // Given - minorBirthDate (16 years old)

        // When
        boolean result = hostService.isOfAge(minorBirthDate);

        // Then
        assertFalse(result);
    }

    /**
     * Prueba el caso de datos inválidos: fecha de nacimiento nula.
     * Verifica que se lance NullPointerException cuando la fecha es nula.
     */
    @Test
    void shouldHandleInvalidData_WhenBirthDateIsNull() {
        // Given - null birthDate

        // When & Then
        assertThrows(NullPointerException.class, () -> hostService.isOfAge(null));
    }

    /**
     * Prueba el caso edge: verificar el límite exacto de 18 años.
     * Verifica que se retorne true cuando la persona tiene exactamente 18 años.
     */
    @Test
    void shouldHandleEdgeCase_WhenPersonIsExactlyEighteen() {
        // Given - exactlyEighteenBirthDate (exactly 18 years old)

        // When
        boolean result = hostService.isOfAge(exactlyEighteenBirthDate);

        // Then
        assertTrue(result);
    }
}
