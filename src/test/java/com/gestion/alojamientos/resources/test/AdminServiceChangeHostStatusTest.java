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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método changeHostStatus del servicio AdminServiceImpl.
 * Prueba la funcionalidad de cambiar el estado de un host.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceChangeHostStatusTest {

    @Mock
    private HostRepo hostRepo;

    @Mock
    private HostMapper hostMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Host host;
    private Host updatedHost;
    private HostDTO hostDTO;
    private Long validHostId;
    private Long invalidHostId;
    private StatesOfHost newStatus;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        invalidHostId = 99L;
        newStatus = StatesOfHost.SUSPENDED;
        
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

        updatedHost = new Host();
        updatedHost.setId(validHostId);
        updatedHost.setEmail("host@test.com");
        updatedHost.setUsername("hostUser");
        updatedHost.setName("Host Name");
        updatedHost.setPhoneNumber("123456789");
        updatedHost.setBirthDate(null);
        updatedHost.setUrlProfilePhoto("http://example.com/photo.jpg");
        updatedHost.setStatus(newStatus);
        updatedHost.setPersonalDescription("Descripción del host");
        updatedHost.setRole(Role.HOST);

        hostDTO = new HostDTO(
                validHostId,              // id
                "host@test.com",          // email
                "hostUser",               // username
                "Host Name",              // name
                "123456789",              // phoneNumber
                new Date(),               // birthDate
                "http://example.com/photo.jpg", // urlProfilePhoto
                newStatus,                // status
                "Descripción del host",   // personalDescription
                List.of(),                // listAccommodationsIds
                List.of(),                // hostCommentIds
                null,                     // financialAccountId
                null,                     // serviceFeeId
                Role.HOST                 // role
        );
    }

    /**
     * Prueba el caso de éxito: cambiar el estado de un host existente.
     * Verifica que se actualice correctamente el estado y se retorne el HostDTO actualizado.
     */
    @Test
    void shouldReturnUpdatedHostDTO_WhenHostExists() {
        // Given
        when(hostRepo.findById(validHostId)).thenReturn(Optional.of(host));
        when(hostRepo.save(any(Host.class))).thenReturn(updatedHost);
        when(hostMapper.toDTO(updatedHost)).thenReturn(hostDTO);

        // When
        HostDTO result = adminService.changeHostStatus(validHostId, newStatus);

        // Then
        assertNotNull(result);
        assertEquals(validHostId, result.id());
        assertEquals(newStatus, result.status());
        verify(hostRepo).findById(validHostId);
        verify(hostRepo).save(host);
        verify(hostMapper).toDTO(updatedHost);
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
                () -> adminService.changeHostStatus(invalidHostId, newStatus)
        );

        assertEquals("Host no encontrado con ID: " + invalidHostId, exception.getMessage());
        verify(hostRepo).findById(invalidHostId);
        verify(hostRepo, never()).save(any());
        verifyNoInteractions(hostMapper);
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo o estado nulo.
     * Verifica que se lance EntityNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(hostRepo.findById(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.changeHostStatus(null, newStatus)
        );

        assertEquals("Host no encontrado con ID: null", exception.getMessage());
        verify(hostRepo).findById(null);
        verify(hostRepo, never()).save(any());
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
                () -> service.changeHostStatus(validHostId, newStatus)
        );

        assertEquals("HostRepo o HostMapper no están disponibles (CHANGE HOST STATUS).", exception.getMessage());
    }
}
