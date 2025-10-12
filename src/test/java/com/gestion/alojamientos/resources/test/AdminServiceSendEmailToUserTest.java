package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.Message.MessageDTO;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método sendEmailToUser del servicio AdminServiceImpl.
 * Prueba la funcionalidad de enviar email a un usuario.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceSendEmailToUserTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private EmailServiceImpl emailServiceImpl;

    @InjectMocks
    private AdminServiceImpl adminService;

    private MessageDTO messageDTO;
    private Guest guest;
    private Long validReceiverId;
    private Long invalidReceiverId;

    @BeforeEach
    void setUp() {
        validReceiverId = 1L;
        invalidReceiverId = 99L;
        
        messageDTO = new MessageDTO(
                1L,                    // id
                1L,                    // senderId
                validReceiverId,       // receiverId
                "Mensaje de prueba",   // text
                null,                  // doc
                new Date(),            // createDate
                1L                     // chatId
        );

        guest = new Guest();
        guest.setId(validReceiverId);
        guest.setEmail("guest@test.com");
        guest.setName("Guest Name");
    }

    /**
     * Prueba el caso de éxito: enviar email a un usuario existente.
     * Verifica que se obtenga el email del usuario y se envíe correctamente.
     */
    @Test
    void shouldSendEmailSuccessfully_WhenUserExists() {
        // Given
        when(guestRepository.findById(validReceiverId)).thenReturn(Optional.of(guest));
        doNothing().when(emailServiceImpl).sendEmaiil(anyString(), any(MessageDTO.class));

        // When
        adminService.sendEmailToUser(messageDTO);

        // Then
        verify(guestRepository).findById(validReceiverId);
        verify(emailServiceImpl).sendEmaiil("guest@test.com", messageDTO);
    }

    /**
     * Prueba el caso de fracaso: cuando el usuario no existe.
     * Verifica que se lance NoSuchElementException cuando el usuario no existe.
     */
    @Test
    void shouldThrowNoSuchElementException_WhenUserDoesNotExist() {
        // Given
        when(guestRepository.findById(invalidReceiverId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                java.util.NoSuchElementException.class,
                () -> adminService.sendEmailToUser(new MessageDTO(
                        1L, 1L, invalidReceiverId, "Test", null, new Date(), 1L
                ))
        );

        verify(guestRepository).findById(invalidReceiverId);
        verifyNoInteractions(emailServiceImpl);
    }

    /**
     * Prueba el caso de datos inválidos: MessageDTO nulo.
     * Verifica que se lance NullPointerException cuando el MessageDTO es nulo.
     */
    @Test
    void shouldHandleInvalidData_WhenInputIsNullOrInvalid() {
        // When & Then
        assertThrows(NullPointerException.class, () -> adminService.sendEmailToUser(null));
        verifyNoInteractions(guestRepository);
        verifyNoInteractions(emailServiceImpl);
    }

    /**
     * Prueba el caso edge: cuando el receiverId es nulo.
     * Verifica que se lance NullPointerException cuando el receiverId es nulo.
     */
    @Test
    void shouldHandleEdgeCase_WhenReceiverIdIsNull() {
        // Given
        MessageDTO messageWithNullReceiver = new MessageDTO(
                1L, 1L, null, "Test", null, new Date(), 1L
        );
        when(guestRepository.findById(null)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                java.util.NoSuchElementException.class,
                () -> adminService.sendEmailToUser(messageWithNullReceiver)
        );

        verify(guestRepository).findById(null);
        verifyNoInteractions(emailServiceImpl);
    }
}
