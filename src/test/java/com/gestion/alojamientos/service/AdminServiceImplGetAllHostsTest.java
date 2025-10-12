package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.mapper.users.HostMapper;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplGetAllHostsTest {

    @Mock
    private HostRepo hostRepo;

    @Mock
    private HostMapper hostMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Host hostEntity;
    private HostDTO hostDTO;

    @BeforeEach
    void setUp() {
        hostEntity = new Host();
        hostEntity.setId(1L);
        hostEntity.setEmail("host@example.com");
        hostEntity.setUsername("hostUser");

        hostDTO = new HostDTO(
                1L,
                "host@example.com",
                "hostUser",
                "Host Name",
                "123456789",
                null,
                null,
                null,
                "Descripción del host",
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                null,
                null
        );
    }

    @Test
    void getAllHosts_ReturnsMappedListSuccessfully() {
        // Arrange
        when(hostRepo.findAll()).thenReturn(List.of(hostEntity));
        when(hostMapper.toDTO(hostEntity)).thenReturn(hostDTO);

        // Act
        List<HostDTO> result = adminService.getAllHosts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("host@example.com", result.get(0).email());
        verify(hostRepo, times(1)).findAll();
        verify(hostMapper, times(1)).toDTO(hostEntity);
    }

    @Test
    void getAllHosts_EmptyList_ReturnsEmpty() {
        // Arrange
        when(hostRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<HostDTO> result = adminService.getAllHosts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(hostRepo, times(1)).findAll();
        verify(hostMapper, never()).toDTO(any());
    }


}
