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
 * Clase de prueba para el método getCommentsBySender del servicio CommentHostServiceImpl.
 * Prueba la funcionalidad de obtener comentarios enviados por un sender específico.
 */
@ExtendWith(MockitoExtension.class)
class CommentHostServiceGetCommentsBySenderTest {

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

    private Long validSenderId;
    private Long nonExistentSenderId;
    private Long nullSenderId;
    private Long minimumSenderId;
    private List<CommentHost> comments;
    private List<CommentHostDTO> commentDTOs;

    @BeforeEach
    void setUp() {
        validSenderId = 1L;
        nonExistentSenderId = 999L;
        nullSenderId = null;
        minimumSenderId = 1L;

        Guest sender = Guest.builder()
                .id(1L)
                .name("Juan Pérez")
                .build();

        Host receiver1 = Host.builder()
                .id(2L)
                .name("María García")
                .build();

        Host receiver2 = Host.builder()
                .id(3L)
                .name("Carlos López")
                .build();

        CommentHost comment1 = CommentHost.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver1)
                .content("Excelente anfitrión, muy recomendado")
                .build();

        CommentHost comment2 = CommentHost.builder()
                .id(2L)
                .sender(sender)
                .receiver(receiver2)
                .content("Muy buena experiencia")
                .build();

        comments = Arrays.asList(comment1, comment2);

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
                "Juan Pérez",                          // senderName
                1L,                                    // senderId
                3L                                     // receiverId
        );

        commentDTOs = Arrays.asList(commentDTO1, commentDTO2);
    }

    /**
     * Prueba el caso de éxito: obtener comentarios enviados por un sender existente.
     * Verifica que se retorne correctamente la lista de CommentHostDTO cuando el sender tiene comentarios.
     */
    @Test
    void shouldReturnCommentHostDTOList_WhenSenderHasComments() {
        // Given
        when(commentHostRepository.findBySenderId(validSenderId)).thenReturn(comments);
        when(mapper.toDto(comments.get(0))).thenReturn(commentDTOs.get(0));
        when(mapper.toDto(comments.get(1))).thenReturn(commentDTOs.get(1));

        // When
        List<CommentHostDTO> result = commentHostService.getCommentsBySender(validSenderId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Excelente anfitrión, muy recomendado", result.get(0).content());
        assertEquals("Juan Pérez", result.get(0).senderName());
        assertEquals(2L, result.get(1).id());
        assertEquals("Muy buena experiencia", result.get(1).content());
        verify(commentHostRepository).findBySenderId(validSenderId);
        verify(mapper, times(2)).toDto(any(CommentHost.class));
    }

    /**
     * Prueba el caso de fracaso: cuando el sender no tiene comentarios.
     * Verifica que se retorne una lista vacía cuando el sender no tiene comentarios.
     */
    @Test
    void shouldReturnEmptyList_WhenSenderHasNoComments() {
        // Given
        when(commentHostRepository.findBySenderId(nonExistentSenderId)).thenReturn(Collections.emptyList());

        // When
        List<CommentHostDTO> result = commentHostService.getCommentsBySender(nonExistentSenderId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentHostRepository).findBySenderId(nonExistentSenderId);
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso de datos inválidos: cuando el senderId es nulo.
     * Verifica que se maneje correctamente cuando el senderId es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenSenderIdIsNull() {
        // Given
        when(commentHostRepository.findBySenderId(nullSenderId)).thenReturn(Collections.emptyList());

        // When
        List<CommentHostDTO> result = commentHostService.getCommentsBySender(nullSenderId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentHostRepository).findBySenderId(nullSenderId);
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso edge: cuando el senderId es el mínimo valor posible.
     * Verifica que se maneje correctamente cuando el senderId es el valor mínimo (1L).
     */
    @Test
    void shouldHandleEdgeCase_WhenSenderIdIsMinimumValue() {
        // Given
        when(commentHostRepository.findBySenderId(minimumSenderId)).thenReturn(comments);
        when(mapper.toDto(comments.get(0))).thenReturn(commentDTOs.get(0));
        when(mapper.toDto(comments.get(1))).thenReturn(commentDTOs.get(1));

        // When
        List<CommentHostDTO> result = commentHostService.getCommentsBySender(minimumSenderId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
        verify(commentHostRepository).findBySenderId(minimumSenderId);
        verify(mapper, times(2)).toDto(any(CommentHost.class));
    }
}
