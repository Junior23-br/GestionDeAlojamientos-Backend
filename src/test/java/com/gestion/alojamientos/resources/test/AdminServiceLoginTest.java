package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.mapper.users.AdminMapper;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesAdmin;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.repository.user.AdminRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método login del servicio AdminServiceImpl.
 * Prueba la funcionalidad de autenticación de administradores.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceLoginTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin admin;
    private AdminDto adminDto;
    private UserLoginDTO validLoginDTO;
    private UserLoginDTO invalidEmailLoginDTO;
    private UserLoginDTO invalidPasswordLoginDTO;
    private String validEmail;
    private String invalidEmail;
    private String validPassword;
    private String invalidPassword;

    @BeforeEach
    void setUp() {
        validEmail = "admin@test.com";
        invalidEmail = "nonexistent@test.com";
        validPassword = "password123";
        invalidPassword = "wrongPassword";
        
        admin = new Admin();
        admin.setId(1L);
        admin.setEmail(validEmail);
        admin.setUsername("admin");
        admin.setPassword("$2a$10$encodedPassword");
        admin.setAccess_level(1);
        admin.setStatesAdmin(StatesAdmin.ACTIVE);
        admin.setRole(Role.ADMIN);

        adminDto = new AdminDto(
                1L,                    // id
                validEmail,            // email
                "admin",               // username
                "Admin Name",          // name
                "123456789",           // phoneNumber
                null,                  // birthDate
                null,                  // urlProfilePhoto
                StatesAdmin.ACTIVE,    // statesAdmin
                "Admin description",   // personalDescription
                null,                  // financialAccountId
                null,                  // serviceFeeId
                Role.ADMIN             // role
        );

        validLoginDTO = new UserLoginDTO(validEmail, validPassword);
        invalidEmailLoginDTO = new UserLoginDTO(invalidEmail, validPassword);
        invalidPasswordLoginDTO = new UserLoginDTO(validEmail, invalidPassword);
    }

    /**
     * Prueba el caso de éxito: login con credenciales válidas.
     * Verifica que se retorne correctamente el UserLoginDTO cuando las credenciales son válidas.
     */
    @Test
    void shouldReturnUserLoginDTO_WhenCredentialsAreValid() throws InvalidElementException {
        // Given
        when(adminRepository.findByEmail(validEmail)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(validPassword, admin.getPassword())).thenReturn(true);
        when(adminMapper.toDTO(admin)).thenReturn(adminDto);

        // When
        UserLoginDTO result = adminService.login(validLoginDTO);

        // Then
        assertNotNull(result);
        assertEquals(validEmail, result.email());
        assertEquals(validPassword, result.password());
        verify(adminRepository).findByEmail(validEmail);
        verify(passwordEncoder).matches(validPassword, admin.getPassword());
        verify(adminMapper).toDTO(admin);
    }

    /**
     * Prueba el caso de fracaso: cuando el email no existe.
     * Verifica que se lance InvalidElementException cuando el email no existe.
     */
    @Test
    void shouldThrowInvalidElementException_WhenEmailDoesNotExist() {
        // Given
        when(adminRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> adminService.login(invalidEmailLoginDTO)
        );

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(adminRepository).findByEmail(invalidEmail);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(adminMapper);
    }

    /**
     * Prueba el caso de datos inválidos: contraseña incorrecta.
     * Verifica que se lance InvalidElementException cuando la contraseña es incorrecta.
     */
    @Test
    void shouldHandleInvalidData_WhenPasswordIsIncorrect() {
        // Given
        when(adminRepository.findByEmail(validEmail)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(invalidPassword, admin.getPassword())).thenReturn(false);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> adminService.login(invalidPasswordLoginDTO)
        );

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(adminRepository).findByEmail(validEmail);
        verify(passwordEncoder).matches(invalidPassword, admin.getPassword());
        verifyNoInteractions(adminMapper);
    }

    /**
     * Prueba el caso edge: cuando el admin está inactivo.
     * Verifica que se maneje correctamente cuando el admin existe pero está inactivo.
     */
    @Test
    void shouldHandleEdgeCase_WhenAdminIsInactive() {
        // Given
        admin.setStatesAdmin(StatesAdmin.INACTIVE);
        when(adminRepository.findByEmail(validEmail)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(validPassword, admin.getPassword())).thenReturn(true);
        when(adminMapper.toDTO(admin)).thenReturn(adminDto);

        // When
        UserLoginDTO result = adminService.login(validLoginDTO);

        // Then
        assertNotNull(result);
        assertEquals(validEmail, result.email());
        assertEquals(validPassword, result.password());
        verify(adminRepository).findByEmail(validEmail);
        verify(passwordEncoder).matches(validPassword, admin.getPassword());
        verify(adminMapper).toDTO(admin);
    }
}
