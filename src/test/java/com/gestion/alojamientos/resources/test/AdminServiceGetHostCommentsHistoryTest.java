package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
import com.gestion.alojamientos.mapper.accomodation.CommentHostMapper;
import com.gestion.alojamientos.model.message.CommentHost;
import com.gestion.alojamientos.repository.accomodation.CommentHostRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getHostCommentsHistory del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener el historial de comentarios de host.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetHostCommentsHistoryTest {

    @Mock
    private CommentHostRepo commentHostRepo;

    @Mock
    private CommentHostMapper commentHostMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Long validHostId;
    private CommentHost comment1;
    private CommentHost comment2;
    private CommentHostDTO commentDTO1;
    private CommentHostDTO commentDTO2;
    private List<CommentHost> comments;
    private List<CommentHostDTO> commentDTOs;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        
        comment1 = new CommentHost();
        comment1.setId(1L);
        comment1.setContent("Excelente anfitrión, muy atento y servicial");

        comment2 = new CommentHost();
        comment2.setId(2L);
        comment2.setContent("Muy buena comunicación, todo perfecto");

        commentDTO1 = new CommentHostDTO(
                1L, "Excelente anfitrión, muy atento y servicial", "Guest Name", 1L, validHostId
        );

        commentDTO2 = new CommentHostDTO(
                2L, "Muy buena comunicación, todo perfecto", "Guest Name", 2L, validHostId
        );

        comments = Arrays.asList(comment1, comment2);
        commentDTOs = Arrays.asList(commentDTO1, commentDTO2);
    }

    /**
     * Prueba el caso de éxito: obtener el historial de comentarios de host.
     * Verifica que se retorne correctamente la lista de CommentHostDTO.
     */
    @Test
    void shouldReturnCommentsHistory_WhenCommentsExist() {
        // Given
        when(commentHostRepo.findByReceiverId(validHostId)).thenReturn(comments);
        when(commentHostMapper.toDto(comment1)).thenReturn(commentDTO1);
        when(commentHostMapper.toDto(comment2)).thenReturn(commentDTO2);

        // When
        List<CommentHostDTO> result = adminService.getHostCommentsHistory(validHostId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(commentDTO1, result.get(0));
        assertEquals(commentDTO2, result.get(1));
        verify(commentHostRepo).findByReceiverId(validHostId);
        verify(commentHostMapper).toDto(comment1);
        verify(commentHostMapper).toDto(comment2);
    }

    /**
     * Prueba el caso de fracaso: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando commentHostRepo o commentHostMapper son null.
     */
    @Test
    void shouldThrowUnsupportedOperationException_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → commentHostRepo y commentHostMapper = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.getHostCommentsHistory(validHostId)
        );

        assertEquals("CommentHostRepo o CommentHostMapper no están disponibles.", exception.getMessage());
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se maneje correctamente cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(commentHostRepo.findByReceiverId(null)).thenReturn(Collections.emptyList());

        // When
        List<CommentHostDTO> result = adminService.getHostCommentsHistory(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentHostRepo).findByReceiverId(null);
        verifyNoInteractions(commentHostMapper);
    }

    /**
     * Prueba el caso edge: cuando no hay comentarios para el host.
     * Verifica que se retorne una lista vacía cuando no existen comentarios.
     */
    @Test
    void shouldHandleEdgeCase_WhenNoCommentsExist() {
        // Given
        when(commentHostRepo.findByReceiverId(validHostId)).thenReturn(Collections.emptyList());

        // When
        List<CommentHostDTO> result = adminService.getHostCommentsHistory(validHostId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentHostRepo).findByReceiverId(validHostId);
        verifyNoInteractions(commentHostMapper);
    }
}
