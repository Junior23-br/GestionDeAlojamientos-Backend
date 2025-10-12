package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.mapper.users.GuestMapper;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.Impl.CloudinaryServiceImpl;
import com.gestion.alojamientos.service.Impl.EmailServiceImpl;
import com.gestion.alojamientos.service.Impl.GuestServiceImpl;
import com.gestion.alojamientos.service.Impl.ResetCodeServiceImpl;

/**
 * Test class for GuestService editGuest method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class GuestServiceEditTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestMapper guestMapper;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private ResetCodeServiceImpl resetCodeService;

    @Mock
    private CloudinaryServiceImpl cloudinaryService;

    @InjectMocks
    private GuestServiceImpl guestService;

    private EditGuestDto validEditGuestDto;
    private Guest existingGuest;
    private GuestDto updatedGuestDto;
    private final Long GUEST_ID = 1L;

    @BeforeEach
    void setUp() {
        // Setup existing guest
        existingGuest = new Guest();
        existingGuest.setId(GUEST_ID);
        existingGuest.setName("Juan Pérez");
        existingGuest.setEmail("juan.perez@email.com");
        existingGuest.setPhoneNumber("+573001234567");
        existingGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        existingGuest.setRole(Role.GUEST);
        existingGuest.setState(StatesOfGuest.ACTIVE);
        existingGuest.setUrlProfilePhoto("https://cloudinary.com/old-image");

        // Setup valid edit DTO
        validEditGuestDto = new EditGuestDto(
                GUEST_ID,
                "Juan Carlos Pérez", // Updated name
                "+573009876543", // Updated phone number
                null // No profile photo update
        );

        // Setup expected updated guest DTO
        updatedGuestDto = new GuestDto(
                GUEST_ID,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Carlos Pérez",
                "+573009876543",
                LocalDate.of(1995, 5, 15),
                null,
                StatesOfGuest.ACTIVE,
                Role.GUEST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    /**
     * Test 1: Éxito - Edición exitosa de un huésped con datos válidos
     */
    @Test
    void editGuest_Success_ShouldReturnUpdatedGuestDto() throws ElementNotFoundException {
        // Arrange
        Guest updatedGuest = new Guest();
        updatedGuest.setId(GUEST_ID);
        updatedGuest.setName("Juan Carlos Pérez");
        updatedGuest.setEmail("juan.perez@email.com");
        updatedGuest.setPhoneNumber("+573009876543");
        updatedGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        updatedGuest.setRole(Role.GUEST);
        updatedGuest.setState(StatesOfGuest.ACTIVE);

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.existsByPhoneNumber(validEditGuestDto.phoneNumber())).thenReturn(false);
        doNothing().when(guestMapper).updateFromDto(validEditGuestDto, existingGuest);
        when(guestRepository.save(existingGuest)).thenReturn(updatedGuest);
        when(guestMapper.toDto(updatedGuest)).thenReturn(updatedGuestDto);

        // Act
        GuestDto result = guestService.editGuest(GUEST_ID, validEditGuestDto);

        // Assert
        assertNotNull(result);
        assertEquals(updatedGuestDto.id(), result.id());
        assertEquals(updatedGuestDto.name(), result.name());
        assertEquals(updatedGuestDto.phoneNumber(), result.phoneNumber());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(guestRepository).existsByPhoneNumber(validEditGuestDto.phoneNumber());
        verify(guestMapper).updateFromDto(validEditGuestDto, existingGuest);
        verify(guestRepository).save(existingGuest);
        verify(guestMapper).toDto(updatedGuest);
    }

    /**
     * Test 2: Fracaso - Huésped no encontrado
     */
    @Test
    void editGuest_GuestNotFound_ShouldThrowElementNotFoundException() {
        // Arrange
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.editGuest(GUEST_ID, validEditGuestDto)
        );

        assertEquals("Usuario no encontrado con ID: " + GUEST_ID, exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 3: Fracaso - Huésped con estado DELETED
     */
    @Test
    void editGuest_GuestDeleted_ShouldThrowElementNotFoundException() {
        // Arrange
        existingGuest.setState(StatesOfGuest.DELETED);
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));

        // Act & Assert
        ElementNotFoundException exception = assertThrows(
                ElementNotFoundException.class,
                () -> guestService.editGuest(GUEST_ID, validEditGuestDto)
        );

        assertEquals("El perfil ha sido eliminado y no puede editar la informacion", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 4: Datos inválidos - Número de teléfono ya registrado por otro usuario
     */
    @Test
    void editGuest_PhoneNumberAlreadyExists_ShouldThrowInvalidElementException() {
        // Arrange
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.existsByPhoneNumber(validEditGuestDto.phoneNumber())).thenReturn(true);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.editGuest(GUEST_ID, validEditGuestDto)
        );

        assertEquals("El numero de telefono ya esta registrado", exception.getMessage());
        verify(guestRepository).findById(GUEST_ID);
        verify(guestRepository).existsByPhoneNumber(validEditGuestDto.phoneNumber());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 5: Caso edge - Edición exitosa con actualización de foto de perfil
     */
    @Test
    void editGuest_SuccessWithProfilePhotoUpdate_ShouldReturnUpdatedGuestDto() throws ElementNotFoundException {
        // Arrange
        MultipartFile newProfilePhoto = new MockMultipartFile(
                "profilePhoto", 
                "new-profile.jpg", 
                "image/jpeg", 
                "new profile image content".getBytes()
        );

        EditGuestDto dtoWithPhoto = new EditGuestDto(
                GUEST_ID,
                "Juan Carlos Pérez",
                "+573009876543",
                newProfilePhoto
        );

        Guest updatedGuestWithPhoto = new Guest();
        updatedGuestWithPhoto.setId(GUEST_ID);
        updatedGuestWithPhoto.setName("Juan Carlos Pérez");
        updatedGuestWithPhoto.setEmail("juan.perez@email.com");
        updatedGuestWithPhoto.setPhoneNumber("+573009876543");
        updatedGuestWithPhoto.setBirthDate(LocalDate.of(1995, 5, 15));
        updatedGuestWithPhoto.setRole(Role.GUEST);
        updatedGuestWithPhoto.setState(StatesOfGuest.ACTIVE);
        updatedGuestWithPhoto.setUrlProfilePhoto("https://cloudinary.com/new-image");

        GuestDto guestDtoWithPhoto = new GuestDto(
                GUEST_ID,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Carlos Pérez",
                "+573009876543",
                LocalDate.of(1995, 5, 15),
                newProfilePhoto,
                StatesOfGuest.ACTIVE,
                Role.GUEST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.existsByPhoneNumber(dtoWithPhoto.phoneNumber())).thenReturn(false);
        when(cloudinaryService.uploadPhoto(newProfilePhoto)).thenReturn("https://cloudinary.com/new-image");
        doNothing().when(guestMapper).updateFromDto(dtoWithPhoto, existingGuest);
        when(guestRepository.save(existingGuest)).thenReturn(updatedGuestWithPhoto);
        when(guestMapper.toDto(updatedGuestWithPhoto)).thenReturn(guestDtoWithPhoto);

        // Act
        GuestDto result = guestService.editGuest(GUEST_ID, dtoWithPhoto);

        // Assert
        assertNotNull(result);
        assertEquals(guestDtoWithPhoto.id(), result.id());
        assertEquals(guestDtoWithPhoto.name(), result.name());
        assertEquals(guestDtoWithPhoto.phoneNumber(), result.phoneNumber());
        assertEquals("https://cloudinary.com/new-image", updatedGuestWithPhoto.getUrlProfilePhoto());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(guestRepository).existsByPhoneNumber(dtoWithPhoto.phoneNumber());
        verify(cloudinaryService).uploadPhoto(newProfilePhoto);
        verify(guestMapper).updateFromDto(dtoWithPhoto, existingGuest);
        verify(guestRepository).save(existingGuest);
        verify(guestMapper).toDto(updatedGuestWithPhoto);
    }

    /**
     * Test 6: Caso edge - Edición exitosa sin cambiar número de teléfono (mismo número)
     */
    @Test
    void editGuest_SuccessWithSamePhoneNumber_ShouldReturnUpdatedGuestDto() throws ElementNotFoundException {
        // Arrange
        EditGuestDto dtoWithSamePhone = new EditGuestDto(
                GUEST_ID,
                "Juan Carlos Pérez",
                "+573001234567", // Same phone number as existing guest
                null
        );

        Guest updatedGuest = new Guest();
        updatedGuest.setId(GUEST_ID);
        updatedGuest.setName("Juan Carlos Pérez");
        updatedGuest.setEmail("juan.perez@email.com");
        updatedGuest.setPhoneNumber("+573001234567");
        updatedGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        updatedGuest.setRole(Role.GUEST);
        updatedGuest.setState(StatesOfGuest.ACTIVE);

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        // No need to check existsByPhoneNumber since it's the same number
        doNothing().when(guestMapper).updateFromDto(dtoWithSamePhone, existingGuest);
        when(guestRepository.save(existingGuest)).thenReturn(updatedGuest);
        when(guestMapper.toDto(updatedGuest)).thenReturn(updatedGuestDto);

        // Act
        GuestDto result = guestService.editGuest(GUEST_ID, dtoWithSamePhone);

        // Assert
        assertNotNull(result);
        assertEquals(updatedGuestDto.id(), result.id());
        assertEquals(updatedGuestDto.name(), result.name());
        assertEquals(updatedGuestDto.phoneNumber(), result.phoneNumber());
        
        verify(guestRepository).findById(GUEST_ID);
        // Should not check existsByPhoneNumber since it's the same number
        verify(guestRepository, never()).existsByPhoneNumber(anyString());
        verify(guestMapper).updateFromDto(dtoWithSamePhone, existingGuest);
        verify(guestRepository).save(existingGuest);
        verify(guestMapper).toDto(updatedGuest);
    }

    /**
     * Test 7: Caso edge - Huésped con estado SUSPENDED (debería permitir edición)
     */
    @Test
    void editGuest_GuestSuspended_ShouldAllowEditAndReturnUpdatedGuestDto() throws ElementNotFoundException {
        // Arrange
        existingGuest.setState(StatesOfGuest.SUSPENDED);
        
        Guest updatedGuest = new Guest();
        updatedGuest.setId(GUEST_ID);
        updatedGuest.setName("Juan Carlos Pérez");
        updatedGuest.setEmail("juan.perez@email.com");
        updatedGuest.setPhoneNumber("+573009876543");
        updatedGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        updatedGuest.setRole(Role.GUEST);
        updatedGuest.setState(StatesOfGuest.SUSPENDED); // State remains suspended

        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.existsByPhoneNumber(validEditGuestDto.phoneNumber())).thenReturn(false);
        doNothing().when(guestMapper).updateFromDto(validEditGuestDto, existingGuest);
        when(guestRepository.save(existingGuest)).thenReturn(updatedGuest);
        when(guestMapper.toDto(updatedGuest)).thenReturn(updatedGuestDto);

        // Act
        GuestDto result = guestService.editGuest(GUEST_ID, validEditGuestDto);

        // Assert
        assertNotNull(result);
        assertEquals(updatedGuestDto.id(), result.id());
        assertEquals(updatedGuestDto.name(), result.name());
        
        verify(guestRepository).findById(GUEST_ID);
        verify(guestRepository).existsByPhoneNumber(validEditGuestDto.phoneNumber());
        verify(guestMapper).updateFromDto(validEditGuestDto, existingGuest);
        verify(guestRepository).save(existingGuest);
        verify(guestMapper).toDto(updatedGuest);
    }
}
