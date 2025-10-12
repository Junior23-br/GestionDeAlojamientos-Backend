package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostCreateDTO;
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
 * Clase de prueba para el método createComment del servicio CommentHostServiceImpl.
 * Prueba la funcionalidad de crear un nuevo comentario hacia un host.
 */
@ExtendWith(MockitoExtension.class)
class CommentHostServiceCreateCommentTest {

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

    private CommentHostCreateDTO validCommentCreateDTO;
    private CommentHostCreateDTO invalidSenderIdDTO;
    private CommentHostCreateDTO invalidReceiverIdDTO;
    private CommentHostCreateDTO emptyContentDTO;
    private Guest sender;
    private Host receiver;
    private CommentHost comment;
    private CommentHostDTO commentDTO;

    @BeforeEach
    void setUp() {
        validCommentCreateDTO = new CommentHostCreateDTO(
                1L,                                    // senderId
                2L,                                    // receiverId
                "Excelente anfitrión, muy recomendado" // content
        );

        invalidSenderIdDTO = new CommentHostCreateDTO(
                999L,                                  // senderId (non-existent)
                2L,                                    // receiverId
                "Comentario válido"                    // content
        );

        invalidReceiverIdDTO = new CommentHostCreateDTO(
                1L,                                    // senderId
                999L,                                  // receiverId (non-existent)
                "Comentario válido"                    // content
        );

        emptyContentDTO = new CommentHostCreateDTO(
                1L,                                    // senderId
                2L,                                    // receiverId
                ""                                     // content (empty)
        );

        sender = Guest.builder()
                .id(1L)
                .name("Juan Pérez")
                .build();

        receiver = Host.builder()
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
     * Prueba el caso de éxito: crear un comentario con datos válidos.
     * Verifica que se cree correctamente el comentario y se retorne el CommentHostDTO.
     */
    @Test
    void shouldReturnCommentHostDTO_WhenCommentCreationIsSuccessful() throws ElementNotFoundException {
        // Given
        when(guestRepository.findById(validCommentCreateDTO.senderId())).thenReturn(Optional.of(sender));
        when(hostRepository.findById(validCommentCreateDTO.receiverId())).thenReturn(Optional.of(receiver));
        when(commentHostRepository.save(any(CommentHost.class))).thenReturn(comment);
        when(mapper.toDto(comment)).thenReturn(commentDTO);

        // When
        CommentHostDTO result = commentHostService.createComment(validCommentCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Excelente anfitrión, muy recomendado", result.content());
        assertEquals("Juan Pérez", result.senderName());
        assertEquals(1L, result.senderId());
        assertEquals(2L, result.receiverId());
        verify(guestRepository).findById(validCommentCreateDTO.senderId());
        verify(hostRepository).findById(validCommentCreateDTO.receiverId());
        verify(commentHostRepository).save(any(CommentHost.class));
        verify(mapper).toDto(comment);
    }

    /**
     * Prueba el caso de fracaso: cuando el sender no existe.
     * Verifica que se lance ElementNotFoundException cuando el senderId no existe.
     */
    @Test
    void shouldThrowElementNotFoundException_WhenSenderNotFound() {
        // Given
        when(guestRepository.findById(invalidSenderIdDTO.senderId())).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> commentHostService.createComment(invalidSenderIdDTO)
        );

        assertEquals("No se encontró el huésped con ID: 999", exception.getMessage());
        verify(guestRepository).findById(invalidSenderIdDTO.senderId());
        verifyNoInteractions(hostRepository);
        verifyNoInteractions(commentHostRepository);
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso de datos inválidos: cuando el receiver no existe.
     * Verifica que se lance ElementNotFoundException cuando el receiverId no existe.
     */
    @Test
    void shouldHandleInvalidData_WhenReceiverNotFound() {
        // Given
        when(guestRepository.findById(invalidReceiverIdDTO.senderId())).thenReturn(Optional.of(sender));
        when(hostRepository.findById(invalidReceiverIdDTO.receiverId())).thenReturn(Optional.empty());

        // When & Then
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> commentHostService.createComment(invalidReceiverIdDTO)
        );

        assertEquals("No se encontró el anfitrión con ID: 999", exception.getMessage());
        verify(guestRepository).findById(invalidReceiverIdDTO.senderId());
        verify(hostRepository).findById(invalidReceiverIdDTO.receiverId());
        verifyNoInteractions(commentHostRepository);
        verifyNoInteractions(mapper);
    }

    /**
     * Prueba el caso edge: cuando el contenido está vacío.
     * Verifica que se maneje correctamente cuando el contenido del comentario está vacío.
     */
    @Test
    void shouldHandleEdgeCase_WhenContentIsEmpty() throws ElementNotFoundException {
        // Given
        CommentHost commentWithEmptyContent = CommentHost.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .content("")
                .build();

        CommentHostDTO commentDTOWithEmptyContent = new CommentHostDTO(
                1L,                                    // id
                "",                                    // content (empty)
                "Juan Pérez",                          // senderName
                1L,                                    // senderId
                2L                                     // receiverId
        );

        when(guestRepository.findById(emptyContentDTO.senderId())).thenReturn(Optional.of(sender));
        when(hostRepository.findById(emptyContentDTO.receiverId())).thenReturn(Optional.of(receiver));
        when(commentHostRepository.save(any(CommentHost.class))).thenReturn(commentWithEmptyContent);
        when(mapper.toDto(commentWithEmptyContent)).thenReturn(commentDTOWithEmptyContent);

        // When
        CommentHostDTO result = commentHostService.createComment(emptyContentDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("", result.content());
        assertEquals("Juan Pérez", result.senderName());
        verify(guestRepository).findById(emptyContentDTO.senderId());
        verify(hostRepository).findById(emptyContentDTO.receiverId());
        verify(commentHostRepository).save(any(CommentHost.class));
        verify(mapper).toDto(commentWithEmptyContent);
    }
}
