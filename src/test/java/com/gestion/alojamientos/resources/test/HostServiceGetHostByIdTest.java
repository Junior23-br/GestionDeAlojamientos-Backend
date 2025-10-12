package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.exception.ElementNotFoundException;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getHostById del servicio HostServiceImpl.
 * Prueba la funcionalidad de obtener un host por su ID.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceGetHostByIdTest {

    @Mock
    private HostRepo hostRepository;

    @Mock
    private HostMapper hostMapper;

    @InjectMocks
    private HostServiceImpl hostService;

    private Host host;
    private HostDTO hostDTO;
    private Long validHostId;
    private Long invalidHostId;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        invalidHostId = 99L;
        
        host = new Host();
        host.setId(validHostId);
        host.setEmail("juan@test.com");
        host.setUsername("juan@test.com");
        host.setName("Juan Pérez");
        host.setPhoneNumber("+571234567890");
        host.setBirthDate(null);
        host.setPersonalDescription("Descripción personal");
        host.setUrlProfilePhoto("http://example.com/photo.jpg");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setRole(Role.HOST);

        hostDTO = new HostDTO(
                validHostId,                     // id
                "juan@test.com",                // email
                "juan@test.com",                // username
                "Juan Pérez",                   // name
                "+571234567890",                // phoneNumber
                new Date(),                     // birthDate
                "http://example.com/photo.jpg", // urlProfilePhoto
                StatesOfHost.ACTIVE,            // status
                "Descripción personal",         // personalDescription
                List.of(),                      // listAccommodationsIds
                List.of(),                      // hostCommentIds
                null,                           // financialAccountId
                null,                           // serviceFeeId
                Role.HOST                       // role
        );
    }

    /**
     * Prueba el caso de éxito: obtener un host existente por ID.
     * Verifica que se retorne correctamente el HostDTO cuando el host existe.
     */
    @Test
    void shouldReturnHostDTO_WhenHostExists() throws ElementNotFoundException {
        // Given
        when(hostRepository.findById(validHostId)).thenReturn(Optional.of(host));
        when(hostMapper.toDTO(host)).thenReturn(hostDTO);

        // When
        HostDTO result = hostService.getHostById(validHostId);

        // Then
        assertNotNull(result);
        assertEquals(validHostId, result.id());
        assertEquals("juan@test.com", result.email());
        assertEquals("Juan Pérez", result.name());
        assertEquals(StatesOfHost.ACTIVE, result.status());
        verify(hostRepository).findById(validHostId);
        verify(hostMapper).toDTO(host);
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
                () -> hostService.getHostById(invalidHostId)
        );

        assertEquals("Host no encontrado con ID: " + invalidHostId, exception.getMessage());
        verify(hostRepository).findById(invalidHostId);
        verifyNoInteractions(hostMapper);
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se lance ElementNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(hostRepository.findById(null)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.getHostById(null)
        );

        assertEquals("Host no encontrado con ID: null", exception.getMessage());
        verify(hostRepository).findById(null);
        verifyNoInteractions(hostMapper);
    }

    /**
     * Prueba el caso edge: cuando el host está eliminado.
     * Verifica que se retorne el HostDTO incluso cuando el host tiene estado DELETED.
     */
    @Test
    void shouldHandleEdgeCase_WhenHostIsDeleted() throws ElementNotFoundException {
        // Given
        host.setStatus(StatesOfHost.DELETED);
        hostDTO = new HostDTO(
                validHostId, "juan@test.com", "juan@test.com", "Juan Pérez",
                "+571234567890", new Date(), "http://example.com/photo.jpg",
                StatesOfHost.DELETED, "Descripción personal", List.of(), List.of(),
                null, null, Role.HOST
        );
        when(hostRepository.findById(validHostId)).thenReturn(Optional.of(host));
        when(hostMapper.toDTO(host)).thenReturn(hostDTO);

        // When
        HostDTO result = hostService.getHostById(validHostId);

        // Then
        assertNotNull(result);
        assertEquals(validHostId, result.id());
        assertEquals(StatesOfHost.DELETED, result.status());
        verify(hostRepository).findById(validHostId);
        verify(hostMapper).toDTO(host);
    }
}
