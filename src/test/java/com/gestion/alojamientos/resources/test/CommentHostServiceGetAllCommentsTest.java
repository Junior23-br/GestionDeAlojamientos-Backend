package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método getAllComments del servicio CommentHostServiceImpl.
 * Prueba la funcionalidad de obtener todos los comentarios del sistema.
 */
@ExtendWith(MockitoExtension.class)
class CommentHostServiceGetAllCommentsTest {

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

    private List<CommentHost> comments;
    private List<CommentHostDTO> commentDTOs;

    @BeforeEach
    void setUp() {
        Guest sender1 = Guest.builder()
                .id(1L)
                .name("Juan Pérez")
                .build();

        Guest sender2 = Guest.builder()
                .id(3L)
                .name("Ana López")
                .build();

        Host receiver1 = Host.builder()
                .id(2L)
                .name("María García")
                .build();

        Host receiver2 = Host.builder()
                .id(4L)
                .name("Carlos López")
                .build();

        CommentHost comment1 = CommentHost.builder()
                .id(1L)
                .sender(sender1)
                .receiver(receiver1)
                .content("Excelente anfitrión, muy recomendado")
                .build();

        CommentHost comment2 = CommentHost.builder()
                .id(2L)
                .sender(sender2)
                .receiver(receiver1)
                .content("Muy buena experiencia")
                .build();

        CommentHost comment3 = CommentHost.builder()
                .id(3L)
                .sender(sender1)
                .receiver(receiver2)
                .content("Servicio excepcional")
                .build();

        comments = Arrays.asList(comment1, comment2, comment3);

        CommentHostDTO commentDTO1 = new CommentHostDTO(
                1L,                                    // id
                "Excelente anfitrión, muy recomendado", // content
                "Juan Pérez",                          // senderName
                1L,                                    // senderId
                2L                                     // receiverId
        );

        CommentHostDTO commentDTO2 = new CommentHostDTO(
                2L,                                    // id
                "Muy buena experiencia",               // content
                "Ana López",                           // senderName
                3L,                                    // senderId
                2L                                     // receiverId
        );

        CommentHostDTO commentDTO3 = new CommentHostDTO(
                3L,                                    // id
                "Servicio excepcional",                 // content
                "Juan Pérez",                          // senderName
                1L,                                    // senderId
                4L                                     // receiverId
        );

        commentDTOs = Arrays.asList(commentDTO1, commentDTO2, commentDTO3);
    }

    /**
     * Prueba el caso de éxito: obtener todos los comentarios cuando existen comentarios.
     * Verifica que se retorne correctamente la lista completa de CommentHostDTO.
     */
    @Test
    void shouldReturnAllCommentHostDTOs_WhenCommentsExist() {
        // Given
        when(commentHostRepository.findAll()).thenReturn(comments);
        when(mapper.toDto(comments.get(0))).thenReturn(commentDTOs.get(0));
        when(mapper.toDto(comments.get(1))).thenReturn(commentDTOs.get(1));
        when(mapper.toDto(comments.get(2))).thenReturn(commentDTOs.get(2));

        // When
        List<CommentHostDTO> result = commentHostService.getAllComments();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Excelente anfitrión, muy recomendado", result.get(0).content());
        assertEquals("Juan Pérez", result.get(0).senderName());
        assertEquals(2L, result.get(1).id());
        assertEquals("Muy buena experiencia", result.get(1).content());
        assertEquals("Ana López", result.get(1).senderName());
        assertEquals(3L, result.get(2).id());
        assertEquals("Servicio excepcional", result.get(2).content());
        assertEquals("Juan Pérez", result.get(2).senderName());
        verify(commentHostRepository).findAll();
        verify(mapper, times(3)).toDto(any(CommentHost.class));
    }

    /**
     * Prueba el caso de fracaso: cuando no existen comentarios en el sistema.
     * Verifica que se retorne una lista vacía cuando no hay comentarios.
     */
    @Test
    void shouldReturnEmptyList_WhenNoCommentsExist() {
        // Given
        when(commentHostRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CommentHostDTO> result = commentHostService.getAllComments();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentHostRepository).findAll();
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso de datos inválidos: cuando el repositorio retorna una lista con elementos nulos.
     * Verifica que se maneje correctamente cuando el repositorio retorna elementos nulos en la lista.
     */
    @Test
    void shouldHandleInvalidData_WhenRepositoryReturnsNullElements() {
        // Given
        List<CommentHost> commentsWithNulls = Arrays.asList(comments.get(0), null, comments.get(1));
        when(commentHostRepository.findAll()).thenReturn(commentsWithNulls);
        when(mapper.toDto(comments.get(0))).thenReturn(commentDTOs.get(0));
        when(mapper.toDto(comments.get(1))).thenReturn(commentDTOs.get(1));

        // When & Then
        assertThrows(NullPointerException.class, () -> commentHostService.getAllComments());
        verify(commentHostRepository).findAll();
        verify(mapper).toDto(comments.get(0));
    }

    /**
     * Prueba el caso edge: cuando existe un solo comentario en el sistema.
     * Verifica que se maneje correctamente cuando solo hay un comentario.
     */
    @Test
    void shouldHandleEdgeCase_WhenOnlyOneCommentExists() {
        // Given
        List<CommentHost> singleComment = Collections.singletonList(comments.get(0));
        
        when(commentHostRepository.findAll()).thenReturn(singleComment);
        when(mapper.toDto(comments.get(0))).thenReturn(commentDTOs.get(0));

        // When
        List<CommentHostDTO> result = commentHostService.getAllComments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Excelente anfitrión, muy recomendado", result.get(0).content());
        assertEquals("Juan Pérez", result.get(0).senderName());
        verify(commentHostRepository).findAll();
        verify(mapper).toDto(comments.get(0));
    }
}
