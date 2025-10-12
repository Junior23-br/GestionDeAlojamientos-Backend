package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.CommentHost.DeleteCommentHostDTO;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.mapper.accomodation.CommentHostMapper;
import com.gestion.alojamientos.model.message.CommentHost;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.accomodation.CommentHostRepo;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.CommentHostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método deleteComment del servicio CommentHostServiceImpl.
 * Prueba la funcionalidad de eliminar un comentario por su ID.
 */
@ExtendWith(MockitoExtension.class)
class CommentHostServiceDeleteCommentTest {

    @Mock
    private CommentHostRepo commentHostRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private HostRepo hostRepository;

    @Mock
    private CommentHostMapper mapper;

    @InjectMocks
    private CommentHostServiceImpl commentHostService;

    private DeleteCommentHostDTO validDeleteDTO;
    private DeleteCommentHostDTO nonExistentDeleteDTO;
    private DeleteCommentHostDTO nullIdDeleteDTO;
    private DeleteCommentHostDTO minimumIdDeleteDTO;
    private CommentHost comment;

    @BeforeEach
    void setUp() {
        validDeleteDTO = new DeleteCommentHostDTO(
                1L,                                    // id
                1L                                     // senderId
        );

        nonExistentDeleteDTO = new DeleteCommentHostDTO(
                999L,                                  // id (non-existent)
                1L                                     // senderId
        );

        nullIdDeleteDTO = new DeleteCommentHostDTO(
                null,                                  // id (null)
                1L                                     // senderId
        );

        minimumIdDeleteDTO = new DeleteCommentHostDTO(
                1L,                                    // id (minimum)
                1L                                     // senderId
        );

        Guest sender = Guest.builder()
                .id(1L)
                .name("Juan Pérez")
                .build();

        Host receiver = Host.builder()
                .id(2L)
                .name("María García")
                .build();

        comment = CommentHost.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .content("Excelente anfitrión, muy recomendado")
                .build();
    }

    /**
     * Prueba el caso de éxito: eliminar un comentario existente.
     * Verifica que se elimine correctamente el comentario cuando existe.
     */
    @Test
    void shouldDeleteCommentSuccessfully_WhenCommentExists() throws ElementNotFoundException {
        // Given
        when(commentHostRepository.findById(validDeleteDTO.id())).thenReturn(Optional.of(comment));

        // When
        commentHostService.deleteComment(validDeleteDTO);

        // Then
        verify(commentHostRepository).findById(validDeleteDTO.id());
        verify(commentHostRepository).delete(comment);
    }

    /**
     * Prueba el caso de fracaso: cuando el comentario no existe.
     * Verifica que se lance ElementNotFoundException cuando el comentario no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenCommentNotFound() {
        // Given
        when(commentHostRepository.findById(nonExistentDeleteDTO.id())).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> commentHostService.deleteComment(nonExistentDeleteDTO)
        );

        assertEquals("Comentario no encontrado con ID: 999", exception.getMessage());
        verify(commentHostRepository).findById(nonExistentDeleteDTO.id());
        verify(commentHostRepository, never()).delete(any(CommentHost.class));
    }

    /**
     * Prueba el caso de datos inválidos: cuando el ID es nulo.
     * Verifica que se lance ElementNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenIdIsNull() {
        // Given
        when(commentHostRepository.findById(nullIdDeleteDTO.id())).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> commentHostService.deleteComment(nullIdDeleteDTO)
        );

        assertEquals("Comentario no encontrado con ID: null", exception.getMessage());
        verify(commentHostRepository).findById(nullIdDeleteDTO.id());
        verify(commentHostRepository, never()).delete(any(CommentHost.class));
    }

    /**
     * Prueba el caso edge: cuando el ID es el mínimo valor posible.
     * Verifica que se maneje correctamente cuando el ID es el valor mínimo (1L).
     */
    @Test
    void shouldHandleEdgeCase_WhenIdIsMinimumValue() throws ElementNotFoundException {
        // Given
        when(commentHostRepository.findById(minimumIdDeleteDTO.id())).thenReturn(Optional.of(comment));

        // When
        commentHostService.deleteComment(minimumIdDeleteDTO);

        // Then
        verify(commentHostRepository).findById(minimumIdDeleteDTO.id());
        verify(commentHostRepository).delete(comment);
    }
}
