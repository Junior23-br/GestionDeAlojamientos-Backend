package com.gestion.alojamientos.resources.test;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método deleteHost del servicio AdminServiceImpl.
 * Prueba la funcionalidad de eliminar (soft delete) un host.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceDeleteHostTest {

    @Mock
    private HostRepo hostRepo;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Host host;
    private Long validHostId;
    private Long invalidHostId;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        invalidHostId = 99L;
        
        host = new Host();
        host.setId(validHostId);
        host.setEmail("host@test.com");
        host.setUsername("hostUser");
        host.setName("Host Name");
        host.setPhoneNumber("123456789");
        host.setBirthDate(null);
        host.setUrlProfilePhoto("http://example.com/photo.jpg");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setPersonalDescription("Descripción del host");
        host.setRole(Role.HOST);
    }

    /**
     * Prueba el caso de éxito: eliminar un host existente.
     * Verifica que se cambie el estado a DELETED y se guarde correctamente.
     */
    @Test
    void shouldDeleteHostSuccessfully_WhenHostExists() {
        // Given
        when(hostRepo.findById(validHostId)).thenReturn(Optional.of(host));
        when(hostRepo.save(any(Host.class))).thenReturn(host);

        // When
        adminService.deleteHost(validHostId);

        // Then
        verify(hostRepo).findById(validHostId);
        verify(hostRepo).save(host);
        assertEquals(StatesOfHost.DELETED, host.getStatus());
    }

    /**
     * Prueba el caso de fracaso: cuando el host no existe.
     * Verifica que se lance EntityNotFoundException cuando el ID no existe.
     */
    @Test
    void shouldThrowEntityNotFoundException_WhenHostDoesNotExist() {
        // Given
        when(hostRepo.findById(invalidHostId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.deleteHost(invalidHostId)
        );

        assertEquals("Host no encontrado con ID: " + invalidHostId, exception.getMessage());
        verify(hostRepo).findById(invalidHostId);
        verify(hostRepo, never()).save(any());
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se lance EntityNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(hostRepo.findById(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.deleteHost(null)
        );

        assertEquals("Host no encontrado con ID: null", exception.getMessage());
        verify(hostRepo).findById(null);
        verify(hostRepo, never()).save(any());
    }

    /**
     * Prueba el caso edge: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando hostRepo es null.
     */
    @Test
    void shouldHandleEdgeCase_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → hostRepo = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.deleteHost(validHostId)
        );

        assertEquals("HostRepo no está disponible (DELETE HOST).", exception.getMessage());
    }
}
