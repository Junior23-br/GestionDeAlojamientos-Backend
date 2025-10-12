package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
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
 * Clase de prueba para el método getCommentById del servicio CommentHostServiceImpl.
 * Prueba la funcionalidad de obtener un comentario por su ID.
 */
@ExtendWith(MockitoExtension.class)
class CommentHostServiceGetCommentByIdTest {

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

    private Long validCommentId;
    private Long nonExistentCommentId;
    private Long nullCommentId;
    private Long minimumCommentId;
    private CommentHost comment;
    private CommentHostDTO commentDTO;

    @BeforeEach
    void setUp() {
        validCommentId = 1L;
        nonExistentCommentId = 999L;
        nullCommentId = null;
        minimumCommentId = 1L;

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

        commentDTO = new CommentHostDTO(
                1L,                                    // id
                "Excelente anfitrión, muy recomendado", // content
                "Juan Pérez",                          // senderName
                1L,                                    // senderId
                2L                                     // receiverId
        );
    }

    /**
     * Prueba el caso de éxito: obtener un comentario existente por ID.
     * Verifica que se retorne correctamente el CommentHostDTO cuando el comentario existe.
     */
    @Test
    void shouldReturnCommentHostDTO_WhenCommentExists() throws ElementNotFoundException {
        // Given
        when(commentHostRepository.findById(validCommentId)).thenReturn(Optional.of(comment));
        when(mapper.toDto(comment)).thenReturn(commentDTO);

        // When
        CommentHostDTO result = commentHostService.getCommentById(validCommentId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Excelente anfitrión, muy recomendado", result.content());
        assertEquals("Juan Pérez", result.senderName());
        assertEquals(1L, result.senderId());
        assertEquals(2L, result.receiverId());
        verify(commentHostRepository).findById(validCommentId);
        verify(mapper).toDto(comment);
    }

    /**
     * Prueba el caso de fracaso: cuando el comentario no existe.
     * Verifica que se lance ElementNotFoundException cuando el ID no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenCommentNotFound() {
        // Given
        when(commentHostRepository.findById(nonExistentCommentId)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> commentHostService.getCommentById(nonExistentCommentId)
        );

        assertEquals("Comentario no encontrado con ID: 999", exception.getMessage());
        verify(commentHostRepository).findById(nonExistentCommentId);
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso de datos inválidos: cuando el ID es nulo.
     * Verifica que se lance ElementNotFoundException cuando el ID es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenIdIsNull() {
        // Given
        when(commentHostRepository.findById(nullCommentId)).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> commentHostService.getCommentById(nullCommentId)
        );

        assertEquals("Comentario no encontrado con ID: null", exception.getMessage());
        verify(commentHostRepository).findById(nullCommentId);
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso edge: cuando el ID es el mínimo valor posible.
     * Verifica que se maneje correctamente cuando el ID es el valor mínimo (1L).
     */
    @Test
    void shouldHandleEdgeCase_WhenIdIsMinimumValue() throws ElementNotFoundException {
        // Given
        when(commentHostRepository.findById(minimumCommentId)).thenReturn(Optional.of(comment));
        when(mapper.toDto(comment)).thenReturn(commentDTO);

        // When
        CommentHostDTO result = commentHostService.getCommentById(minimumCommentId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Excelente anfitrión, muy recomendado", result.content());
        assertEquals("Juan Pérez", result.senderName());
        verify(commentHostRepository).findById(minimumCommentId);
        verify(mapper).toDto(comment);
    }
}
