package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.mapper.users.HostMapper;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceGetHostByIdTest {

    @Mock
    private HostRepo hostRepo;

    @Mock
    private HostMapper hostMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Host host;
    private HostDTO hostDTO;

    @BeforeEach
    void setUp() {
        host = new Host();
        host.setId(1L);
        host.setEmail("host@test.com");

        hostDTO = new HostDTO(
                1L,                      // id
                "host@test.com",         // email
                "hostUser",              // username
                "Host Name",             // name
                "123456789",             // phoneNumber
                null,                    // birthDate
                null,                    // urlProfilePhoto
                null,                    // status (StatesOfHost)
                "Descripción del host",  // personalDescription
                List.of(),               // listAccommodationsIds
                List.of(),               // hostCommentIds
                null,                    // financialAccountId
                null,                    // serviceFeeId
                null                     // role
        );
    }


    @Test
    void getHostById_ShouldReturnHostDTO_WhenHostExists() {
        // Arrange
        when(hostRepo.findById(1L)).thenReturn(Optional.of(host));
        when(hostMapper.toDTO(host)).thenReturn(hostDTO);

        // Act
        HostDTO result = adminService.getHostById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("host@test.com", result.email());
        verify(hostRepo).findById(1L);
        verify(hostMapper).toDTO(host);
    }

    @Test
    void getHostById_ShouldThrowEntityNotFoundException_WhenHostDoesNotExist() {
        // Arrange
        when(hostRepo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.getHostById(99L)
        );

        assertEquals("Host no encontrado con ID: 99", exception.getMessage());
        verify(hostRepo).findById(99L);
        verifyNoInteractions(hostMapper);
    }

    @Test
    void getHostById_ShouldThrowUnsupportedOperationException_WhenDependenciesAreNull() {
        // Arrange
        AdminServiceImpl service = new AdminServiceImpl();
        // no se inyectan dependencias → hostRepo y hostMapper = null

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> service.getHostById(1L));
    }
}
