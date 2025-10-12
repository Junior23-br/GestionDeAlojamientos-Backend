package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.Host.DeleteHostDTO;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método deleteHost del servicio HostServiceImpl.
 * Prueba la funcionalidad de eliminar (soft delete) un host.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceDeleteHostTest {

    @Mock
    private HostRepo hostRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private HostServiceImpl hostService;

    private Host host;
    private DeleteHostDTO validDeleteHostDTO;
    private DeleteHostDTO invalidPasswordDeleteHostDTO;
    private Long validHostId;
    private Long invalidHostId;
    private String validPassword;
    private String invalidPassword;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        invalidHostId = 99L;
        validPassword = "Password123";
        invalidPassword = "wrongPassword";
        
        host = new Host();
        host.setId(validHostId);
        host.setEmail("juan@test.com");
        host.setUsername("juan@test.com");
        host.setName("Juan Pérez");
        host.setPhoneNumber("+571234567890");
        host.setBirthDate(new Date());
        host.setPersonalDescription("Descripción personal");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setRole(Role.HOST);
        host.setPassword("$2a$10$encodedPassword");

        validDeleteHostDTO = new DeleteHostDTO(
                validHostId,                     // id
                validPassword                    // password
        );

        invalidPasswordDeleteHostDTO = new DeleteHostDTO(
                validHostId,                     // id
                invalidPassword                  // password
        );
    }

    /**
     * Prueba el caso de éxito: eliminar un host existente con contraseña correcta.
     * Verifica que se cambie el estado a DELETED y se guarde correctamente.
     */
    @Test
    void shouldDeleteHostSuccessfully_WhenPasswordIsCorrect() throws ElementNotFoundException, InvalidElementException {
        // Given
        when(hostRepository.findById(validHostId)).thenReturn(Optional.of(host));
        when(passwordEncoder.matches(validPassword, host.getPassword())).thenReturn(true);
        when(hostRepository.save(any(Host.class))).thenReturn(host);

        // When
        hostService.deleteHost(validHostId, validDeleteHostDTO);

        // Then
        verify(hostRepository).findById(validHostId);
        verify(passwordEncoder).matches(validPassword, host.getPassword());
        verify(hostRepository).save(host);
        assertEquals(StatesOfHost.DELETED, host.getStatus());
    }

    /**
     * Prueba el caso de fracaso: cuando el host no existe.
     * Verifica que se lance ElementNotFoundException cuando el ID no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenHostDoesNotExist() {
        // Given
        when(hostRepository.findById(invalidHostId)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.deleteHost(invalidHostId, validDeleteHostDTO)
        );

        assertEquals("Host no encontrado con ID: " + invalidHostId, exception.getMessage());
        verify(hostRepository).findById(invalidHostId);
        verifyNoInteractions(passwordEncoder);
        verify(hostRepository, never()).save(any());
    }

    /**
     * Prueba el caso de datos inválidos: contraseña incorrecta.
     * Verifica que se lance InvalidElementException cuando la contraseña es incorrecta.
     */
    @Test
    void shouldHandleInvalidData_WhenPasswordIsIncorrect() {
        // Given
        when(hostRepository.findById(validHostId)).thenReturn(Optional.of(host));
        when(passwordEncoder.matches(invalidPassword, host.getPassword())).thenReturn(false);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> hostService.deleteHost(validHostId, invalidPasswordDeleteHostDTO)
        );

        assertEquals("Contraseña incorrecta.", exception.getMessage());
        verify(hostRepository).findById(validHostId);
        verify(passwordEncoder).matches(invalidPassword, host.getPassword());
        verify(hostRepository, never()).save(any());
    }

    /**
     * Prueba el caso edge: cuando el ID es nulo.
     * Verifica que se lance ElementNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleEdgeCase_WhenIdIsNull() {
        // Given
        when(hostRepository.findById(null)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.deleteHost(null, validDeleteHostDTO)
        );

        assertEquals("Host no encontrado con ID: null", exception.getMessage());
        verify(hostRepository).findById(null);
        verifyNoInteractions(passwordEncoder);
        verify(hostRepository, never()).save(any());
    }
}
