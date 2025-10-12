package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.dto.Host.HostUpdateDTO;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método editHost del servicio HostServiceImpl.
 * Prueba la funcionalidad de editar los datos de un host existente.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceEditHostTest {

    @Mock
    private HostRepo hostRepository;

    @Mock
    private HostMapper hostMapper;

    @InjectMocks
    private HostServiceImpl hostService;

    private Host host;
    private Host updatedHost;
    private HostDTO hostDTO;
    private HostUpdateDTO validHostUpdateDTO;
    private HostUpdateDTO invalidHostUpdateDTO;
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
        host.setBirthDate(new Date());
        host.setPersonalDescription("Descripción original");
        host.setUrlProfilePhoto("http://example.com/old-photo.jpg");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setRole(Role.HOST);

        updatedHost = new Host();
        updatedHost.setId(validHostId);
        updatedHost.setEmail("juan@test.com");
        updatedHost.setUsername("juan@test.com");
        updatedHost.setName("Juan Carlos Pérez");
        updatedHost.setPhoneNumber("+579876543210");
        updatedHost.setBirthDate(new Date());
        updatedHost.setPersonalDescription("Nueva descripción personal");
        updatedHost.setUrlProfilePhoto("http://example.com/new-photo.jpg");
        updatedHost.setStatus(StatesOfHost.ACTIVE);
        updatedHost.setRole(Role.HOST);

        validHostUpdateDTO = new HostUpdateDTO(
                validHostId,                     // id
                "Juan Carlos Pérez",             // name
                "+579876543210",                 // phoneNumber
                "Nueva descripción personal",    // personalDescription
                "http://example.com/new-photo.jpg" // urlProfilePhoto
        );

        invalidHostUpdateDTO = new HostUpdateDTO(
                invalidHostId,                   // id
                "Nombre Actualizado",            // name
                "+571112223334",                 // phoneNumber
                "Nueva descripción",             // personalDescription
                "http://example.com/photo.jpg"   // urlProfilePhoto
        );

        hostDTO = new HostDTO(
                validHostId,                     // id
                "juan@test.com",                 // email
                "juan@test.com",                 // username
                "Juan Carlos Pérez",             // name
                "+579876543210",                 // phoneNumber
                new Date(),                      // birthDate
                "http://example.com/new-photo.jpg", // urlProfilePhoto
                StatesOfHost.ACTIVE,             // status
                "Nueva descripción personal",    // personalDescription
                List.of(),                       // listAccommodationsIds
                List.of(),                       // hostCommentIds
                null,                            // financialAccountId
                null,                            // serviceFeeId
                Role.HOST                        // role
        );
    }

    /**
     * Prueba el caso de éxito: editar un host existente con datos válidos.
     * Verifica que se actualicen correctamente los datos y se retorne el HostDTO actualizado.
     */
    @Test
    void shouldReturnUpdatedHostDTO_WhenEditIsSuccessful() throws ElementNotFoundException, InvalidElementException {
        // Given
        when(hostRepository.findById(validHostId)).thenReturn(Optional.of(host));
        when(hostRepository.save(any(Host.class))).thenReturn(updatedHost);
        when(hostMapper.toDTO(updatedHost)).thenReturn(hostDTO);

        // When
        HostDTO result = hostService.editHost(validHostId, validHostUpdateDTO);

        // Then
        assertNotNull(result);
        assertEquals(validHostId, result.id());
        assertEquals("Juan Carlos Pérez", result.name());
        assertEquals("+579876543210", result.phoneNumber());
        assertEquals("Nueva descripción personal", result.personalDescription());
        verify(hostRepository).findById(validHostId);
        verify(hostRepository).save(host);
        verify(hostMapper).toDTO(updatedHost);
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
                () -> hostService.editHost(invalidHostId, invalidHostUpdateDTO)
        );

        assertEquals("Host no encontrado con ID: " + invalidHostId, exception.getMessage());
        verify(hostRepository).findById(invalidHostId);
        verify(hostRepository, never()).save(any());
        verifyNoInteractions(hostMapper);
    }

    /**
     * Prueba el caso de datos inválidos: cuando el host está eliminado.
     * Verifica que se lance ElementNotFoundException cuando el host tiene estado DELETED.
     */
    @Test
    void shouldHandleInvalidData_WhenHostIsDeleted() {
        // Given
        host.setStatus(StatesOfHost.DELETED);
        when(hostRepository.findById(validHostId)).thenReturn(Optional.of(host));

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> hostService.editHost(validHostId, validHostUpdateDTO)
        );

        assertEquals("El perfil ha sido eliminado y no puede editar la información.", exception.getMessage());
        verify(hostRepository).findById(validHostId);
        verify(hostRepository, never()).save(any());
        verifyNoInteractions(hostMapper);
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
                () -> hostService.editHost(null, validHostUpdateDTO)
        );

        assertEquals("Host no encontrado con ID: null", exception.getMessage());
        verify(hostRepository).findById(null);
        verify(hostRepository, never()).save(any());
        verifyNoInteractions(hostMapper);
    }
}
