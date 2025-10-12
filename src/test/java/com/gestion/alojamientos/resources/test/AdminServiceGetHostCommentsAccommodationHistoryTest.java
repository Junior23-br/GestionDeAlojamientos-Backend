package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.CommentAccomodation.AccommodionCommentDTO;
import com.gestion.alojamientos.mapper.accomodation.CommentAccomodationMapper;
import com.gestion.alojamientos.model.accomodation.CommentAccomodation;
import com.gestion.alojamientos.repository.accomodation.CommentAccomodationRepo;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getHostCommentsAccommodationHistory del servicio AdminServiceImpl.
 * Prueba la funcionalidad de obtener el historial de comentarios de alojamientos de un host.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceGetHostCommentsAccommodationHistoryTest {

    @Mock
    private CommentAccomodationRepo commentAccomodationRepo;

    @Mock
    private CommentAccomodationMapper commentAccommodationMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Long validHostId;
    private CommentAccomodation comment1;
    private CommentAccomodation comment2;
    private AccommodionCommentDTO commentDTO1;
    private AccommodionCommentDTO commentDTO2;
    private List<CommentAccomodation> comments;
    private List<AccommodionCommentDTO> commentDTOs;

    @BeforeEach
    void setUp() {
        validHostId = 1L;
        
        comment1 = new CommentAccomodation();
        comment1.setId(1L);
        comment1.setText("Excelente alojamiento, muy limpio y cómodo");
        comment1.setCreationDate(LocalDateTime.now());
        comment1.setIsVisible(true);

        comment2 = new CommentAccomodation();
        comment2.setId(2L);
        comment2.setText("Muy buena ubicación, cerca de todo");
        comment2.setCreationDate(LocalDateTime.now());
        comment2.setIsVisible(true);

        commentDTO1 = new AccommodionCommentDTO(
                1L, "Excelente alojamiento, muy limpio y cómodo", LocalDateTime.now(),
                "Guest Name", true, 1L, null
        );

        commentDTO2 = new AccommodionCommentDTO(
                2L, "Muy buena ubicación, cerca de todo", LocalDateTime.now(),
                "Guest Name", true, 2L, null
        );

        comments = Arrays.asList(comment1, comment2);
        commentDTOs = Arrays.asList(commentDTO1, commentDTO2);
    }

    /**
     * Prueba el caso de éxito: obtener el historial de comentarios de alojamientos.
     * Verifica que se retorne correctamente la lista de AccommodionCommentDTO.
     */
    @Test
    void shouldReturnCommentsHistory_WhenCommentsExist() {
        // Given
        when(commentAccomodationRepo.findByAuthorId(validHostId)).thenReturn(comments);
        when(commentAccommodationMapper.toDTO(comment1)).thenReturn(commentDTO1);
        when(commentAccommodationMapper.toDTO(comment2)).thenReturn(commentDTO2);

        // When
        List<AccommodionCommentDTO> result = adminService.getHostCommentsAccommodationHistory(validHostId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(commentDTO1, result.get(0));
        assertEquals(commentDTO2, result.get(1));
        verify(commentAccomodationRepo).findByAuthorId(validHostId);
        verify(commentAccommodationMapper).toDTO(comment1);
        verify(commentAccommodationMapper).toDTO(comment2);
    }

    /**
     * Prueba el caso de fracaso: cuando las dependencias no están disponibles.
     * Verifica que se lance UnsupportedOperationException cuando commentAccomodationRepo o commentAccommodationMapper son null.
     */
    @Test
    void shouldThrowUnsupportedOperationException_WhenDependenciesAreNotAvailable() {
        // Given
        AdminServiceImpl service = new AdminServiceImpl();
        // No se inyectan dependencias → commentAccomodationRepo y commentAccommodationMapper = null

        // When & Then
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> service.getHostCommentsAccommodationHistory(validHostId)
        );

        assertEquals("CommentAccomodationRepo o CommentAccommodationMapper no están disponibles.", exception.getMessage());
    }

    /**
     * Prueba el caso de datos inválidos: ID nulo.
     * Verifica que se maneje correctamente cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // Given
        when(commentAccomodationRepo.findByAuthorId(null)).thenReturn(Collections.emptyList());

        // When
        List<AccommodionCommentDTO> result = adminService.getHostCommentsAccommodationHistory(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentAccomodationRepo).findByAuthorId(null);
        verifyNoInteractions(commentAccommodationMapper);
    }

    /**
     * Prueba el caso edge: cuando no hay comentarios para el host.
     * Verifica que se retorne una lista vacía cuando no existen comentarios.
     */
    @Test
    void shouldHandleEdgeCase_WhenNoCommentsExist() {
        // Given
        when(commentAccomodationRepo.findByAuthorId(validHostId)).thenReturn(Collections.emptyList());

        // When
        List<AccommodionCommentDTO> result = adminService.getHostCommentsAccommodationHistory(validHostId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentAccomodationRepo).findByAuthorId(validHostId);
        verifyNoInteractions(commentAccommodationMapper);
    }
}
