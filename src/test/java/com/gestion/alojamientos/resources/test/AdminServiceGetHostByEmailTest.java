package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.mapper.users.HostMapper;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getHostByEmail del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener un host por su email.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetHostByEmailTest {

    @Mock
    private HostRepo hostRepo;

    @Mock
    private HostMapper hostMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Host host;
    private HostDTO hostDTO;
    private String validEmail;
    private String invalidEmail;

    @BeforeEach
    void setUp() {
        validEmail = "host@test.com";
        invalidEmail = "nonexistent@test.com";
        
        host = new Host();
        host.setId(1L);
        host.setEmail(validEmail);
        host.setUsername("hostUser");
        host.setName("Host Name");
        host.setPhoneNumber("123456789");
        host.setBirthDate(new Date());
        host.setUrlProfilePhoto("http://example.com/photo.jpg");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setPersonalDescription("Descripción del host");
        host.setRole(Role.HOST);

        hostDTO = new HostDTO(
                1L,                      // id
                validEmail,              // email
                "hostUser",              // username
                "Host Name",             // name
                "123456789",             // phoneNumber
                new Date(),              // birthDate
                "http://example.com/photo.jpg", // urlProfilePhoto
                StatesOfHost.ACTIVE,     // status
                "Descripción del host",  // personalDescription
                List.of(),               // listAccommodationsIds
                List.of(),               // hostCommentIds
                null,                    // financialAccountId
                null,                    // serviceFeeId
                Role.HOST                // role
        );
    }

    /**
     * Prueba el caso de éxito: obtener un host existente por email.
     * Verifica que se retorne correctamente el HostDTO cuando el host existe.
     */
    @Test
    void shouldReturnHostDTO_WhenHostExists() {
        // Given
        when(hostRepo.findByEmail(validEmail)).thenReturn(Optional.of(host));
        when(hostMapper.toDTO(host)).thenReturn(hostDTO);

        // When
        HostDTO result = adminService.getHostByEmail(validEmail);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(validEmail, result.email());
        assertEquals("Host Name", result.name());
        assertEquals(StatesOfHost.ACTIVE, result.status());
        verify(hostRepo).findByEmail(validEmail);
        verify(hostMapper).toDTO(host);
    }

    /**
     * Prueba el caso de fracaso: cuando el host no existe.
     * Verifica que se lance EntityNotFoundException cuando el email no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenHostDoesNotExist() {
        // Given
        when(hostRepo.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getHostByEmail(invalidEmail)
        );

        assertEquals("Host no encontrado con email: " + invalidEmail, exception.getMessage());
        verify(hostRepo).findByEmail(invalidEmail);
        verifyNoInteractions(hostMapper);
    }

    /**
     * Prueba el caso de datos inválidos: email nulo o vacío.
     * Verifica que se lance EntityNotFoundException cuando el email es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(hostRepo.findByEmail(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getHostByEmail(null)
        );

        assertEquals("Host no encontrado con email: null", exception.getMessage());
        verify(hostRepo).findByEmail(null);
        verifyNoInteractions(hostMapper);
    }

    /**
     * Prueba el caso edge: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando hostRepo o hostMapper son null.
     */
    @Test
    void shouldHandleEdgeCase_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → hostRepo y hostMapper = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.getHostByEmail(validEmail)
        );

        assertEquals("HostRepo o HostMapper no están disponibles (GET HOST BY EMAIL).", exception.getMessage());
    }
}
