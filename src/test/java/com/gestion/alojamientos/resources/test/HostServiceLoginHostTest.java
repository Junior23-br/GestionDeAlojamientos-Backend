package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.mapper.users.HostMapper;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.HostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método loginHost del servicio HostServiceImpl.
 * Prueba la funcionalidad de autenticación de hosts.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceLoginHostTest {

    @Mock
    private HostRepo hostRepository;

    @Mock
    private HostMapper hostMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private HostServiceImpl hostService;

    private Host host;
    private HostDTO hostDTO;
    private UserLoginDTO validLoginDTO;
    private UserLoginDTO invalidEmailLoginDTO;
    private UserLoginDTO invalidPasswordLoginDTO;
    private String validEmail;
    private String invalidEmail;
    private String validPassword;
    private String invalidPassword;

    @BeforeEach
    void setUp() {
        validEmail = "juan@test.com";
        invalidEmail = "nonexistent@test.com";
        validPassword = "Password123";
        invalidPassword = "wrongPassword";
        
        host = new Host();
        host.setId(1L);
        host.setEmail(validEmail);
        host.setUsername(validEmail);
        host.setName("Juan Pérez");
        host.setPhoneNumber("+571234567890");
        host.setBirthDate(null);
        host.setPersonalDescription("Descripción personal");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setRole(Role.HOST);
        host.setPassword("$2a$10$encodedPassword");

        hostDTO = new HostDTO(
                1L,                             // id
                validEmail,                     // email
                validEmail,                     // username
                "Juan Pérez",                   // name
                "+571234567890",                // phoneNumber
                new Date(),                     // birthDate
                null,                           // urlProfilePhoto
                StatesOfHost.ACTIVE,            // status
                "Descripción personal",         // personalDescription
                List.of(),                      // listAccommodationsIds
                List.of(),                      // hostCommentIds
                null,                           // financialAccountId
                null,                           // serviceFeeId
                Role.HOST                       // role
        );

        validLoginDTO = new UserLoginDTO(validEmail, validPassword);
        invalidEmailLoginDTO = new UserLoginDTO(invalidEmail, validPassword);
        invalidPasswordLoginDTO = new UserLoginDTO(validEmail, invalidPassword);
    }

    /**
     * Prueba el caso de éxito: login con credenciales válidas.
     * Verifica que se retorne correctamente el HostDTO cuando las credenciales son válidas.
     */
    @Test
    void shouldReturnHostDTO_WhenCredentialsAreValid() throws ElementNotFoundException, InvalidElementException {
        // Given
        when(hostRepository.findByEmail(validEmail)).thenReturn(Optional.of(host));
        when(passwordEncoder.matches(validPassword, host.getPassword())).thenReturn(true);
        when(hostMapper.toDTO(host)).thenReturn(hostDTO);

        // When
        HostDTO result = hostService.loginHost(validLoginDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(validEmail, result.email());
        assertEquals("Juan Pérez", result.name());
        assertEquals(StatesOfHost.ACTIVE, result.status());
        verify(hostRepository).findByEmail(validEmail);
        verify(passwordEncoder).matches(validPassword, host.getPassword());
        verify(hostMapper).toDTO(host);
    }

    /**
     * Prueba el caso de fracaso: cuando el email no existe.
     * Verifica que se lance ElementNotFoundException cuando el email no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenEmailDoesNotExist() {
        // Given
        when(hostRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.loginHost(invalidEmailLoginDTO)
        );

        assertEquals("Host no encontrado con email: " + invalidEmail, exception.getMessage());
        verify(hostRepository).findByEmail(invalidEmail);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(hostMapper);
    }

    /**
     * Prueba el caso de datos inválidos: contraseña incorrecta.
     * Verifica que se lance InvalidElementException cuando la contraseña es incorrecta.
     */
    @Test
    void shouldHandleInvalidData_WhenPasswordIsIncorrect() {
        // Given
        when(hostRepository.findByEmail(validEmail)).thenReturn(Optional.of(host));
        when(passwordEncoder.matches(invalidPassword, host.getPassword())).thenReturn(false);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> hostService.loginHost(invalidPasswordLoginDTO)
        );

        assertEquals("Contraseña incorrecta.", exception.getMessage());
        verify(hostRepository).findByEmail(validEmail);
        verify(passwordEncoder).matches(invalidPassword, host.getPassword());
        verifyNoInteractions(hostMapper);
    }

    /**
     * Prueba el caso edge: cuando el host está eliminado.
     * Verifica que se lance ElementNotFoundException cuando el host tiene estado DELETED.
     */
    @Test
    void shouldHandleEdgeCase_WhenHostIsDeleted() {
        // Given
        host.setStatus(StatesOfHost.DELETED);
        when(hostRepository.findByEmail(validEmail)).thenReturn(Optional.of(host));
        when(passwordEncoder.matches(validPassword, host.getPassword())).thenReturn(true);

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.loginHost(validLoginDTO)
        );

        assertEquals("El perfil ha sido eliminado.", exception.getMessage());
        verify(hostRepository).findByEmail(validEmail);
        verify(passwordEncoder).matches(validPassword, host.getPassword());
        verifyNoInteractions(hostMapper);
    }
}
