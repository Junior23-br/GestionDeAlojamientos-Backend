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
 * Clase de prueba para el método getCommentsByReceiver del servicio CommentHostServiceImpl.
 * Prueba la funcionalidad de obtener comentarios recibidos por un receiver específico.
 */
@ExtendWith(MockitoExtension.class)
class CommentHostServiceGetCommentsByReceiverTest {

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

    private Long validReceiverId;
    private Long nonExistentReceiverId;
    private Long nullReceiverId;
    private Long minimumReceiverId;
    private List<CommentHost> comments;
    private List<CommentHostDTO> commentDTOs;

    @BeforeEach
    void setUp() {
        validReceiverId = 2L;
        nonExistentReceiverId = 999L;
        nullReceiverId = null;
        minimumReceiverId = 1L;

        Guest sender1 = Guest.builder()
                .id(1L)
                .name("Juan Pérez")
                .build();

        Guest sender2 = Guest.builder()
                .id(3L)
                .name("Ana López")
                .build();

        Host receiver = Host.builder()
                .id(2L)
                .name("María García")
                .build();

        CommentHost comment1 = CommentHost.builder()
                .id(1L)
                .sender(sender1)
                .receiver(receiver)
                .content("Excelente anfitrión, muy recomendado")
                .build();

        CommentHost comment2 = CommentHost.builder()
                .id(2L)
                .sender(sender2)
                .receiver(receiver)
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
                "Ana López",                           // senderName
                3L,                                    // senderId
                2L                                     // receiverId
        );

        commentDTOs = Arrays.asList(commentDTO1, commentDTO2);
    }

    /**
     * Prueba el caso de éxito: obtener comentarios recibidos por un receiver existente.
     * Verifica que se retorne correctamente la lista de CommentHostDTO cuando el receiver tiene comentarios.
     */
    @Test
    void shouldReturnCommentHostDTOList_WhenReceiverHasComments() {
        // Given
        when(commentHostRepository.findByReceiverId(validReceiverId)).thenReturn(comments);
        when(mapper.toDto(comments.get(0))).thenReturn(commentDTOs.get(0));
        when(mapper.toDto(comments.get(1))).thenReturn(commentDTOs.get(1));

        // When
        List<CommentHostDTO> result = commentHostService.getCommentsByReceiver(validReceiverId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Excelente anfitrión, muy recomendado", result.get(0).content());
        assertEquals("Juan Pérez", result.get(0).senderName());
        assertEquals(2L, result.get(1).id());
        assertEquals("Muy buena experiencia", result.get(1).content());
        assertEquals("Ana López", result.get(1).senderName());
        verify(commentHostRepository).findByReceiverId(validReceiverId);
        verify(mapper, times(2)).toDto(any(CommentHost.class));
    }

    /**
     * Prueba el caso de fracaso: cuando el receiver no tiene comentarios.
     * Verifica que se retorne una lista vacía cuando el receiver no tiene comentarios.
     */
    @Test
    void shouldReturnEmptyList_WhenReceiverHasNoComments() {
        // Given
        when(commentHostRepository.findByReceiverId(nonExistentReceiverId)).thenReturn(Collections.emptyList());

        // When
        List<CommentHostDTO> result = commentHostService.getCommentsByReceiver(nonExistentReceiverId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentHostRepository).findByReceiverId(nonExistentReceiverId);
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso de datos inválidos: cuando el receiverId es nulo.
     * Verifica que se maneje correctamente cuando el receiverId es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenReceiverIdIsNull() {
        // Given
        when(commentHostRepository.findByReceiverId(nullReceiverId)).thenReturn(Collections.emptyList());

        // When
        List<CommentHostDTO> result = commentHostService.getCommentsByReceiver(nullReceiverId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentHostRepository).findByReceiverId(nullReceiverId);
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso edge: cuando el receiverId es el mínimo valor posible.
     * Verifica que se maneje correctamente cuando el receiverId es el valor mínimo (1L).
     */
    @Test
    void shouldHandleEdgeCase_WhenReceiverIdIsMinimumValue() {
        // Given
        when(commentHostRepository.findByReceiverId(minimumReceiverId)).thenReturn(comments);
        when(mapper.toDto(comments.get(0))).thenReturn(commentDTOs.get(0));
        when(mapper.toDto(comments.get(1))).thenReturn(commentDTOs.get(1));

        // When
        List<CommentHostDTO> result = commentHostService.getCommentsByReceiver(minimumReceiverId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
        verify(commentHostRepository).findByReceiverId(minimumReceiverId);
        verify(mapper, times(2)).toDto(any(CommentHost.class));
    }
}
