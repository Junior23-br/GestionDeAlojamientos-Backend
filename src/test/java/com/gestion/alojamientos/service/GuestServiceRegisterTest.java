package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
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
 * Test class for GuestService registerGuest method
 * Tests success, failure, invalid data, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class GuestServiceRegisterTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestMapper guestMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private ResetCodeServiceImpl resetCodeService;

    @Mock
    private CloudinaryServiceImpl cloudinaryService;

    @InjectMocks
    private GuestServiceImpl guestService;

    private CreateGuestDto validCreateGuestDto;
    private Guest validGuest;
    private GuestDto validGuestDto;

    @BeforeEach
    void setUp() {
        // Setup valid test data
        validCreateGuestDto = new CreateGuestDto(
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                "juan.perez@email.com",
                "Password123",
                Role.GUEST,
                null
        );

        validGuest = new Guest();
        validGuest.setId(1L);
        validGuest.setName("Juan Pérez");
        validGuest.setEmail("juan.perez@email.com");
        validGuest.setPhoneNumber("+573001234567");
        validGuest.setBirthDate(LocalDate.of(1995, 5, 15));
        validGuest.setRole(Role.GUEST);
        validGuest.setState(StatesOfGuest.ACTIVE);

        validGuestDto = new GuestDto(
                1L,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Pérez",
                "+573001234567",
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
     * Test 1: Éxito - Registro exitoso de un huésped con datos válidos
     */
    @Test
    void registerGuest_Success_ShouldReturnGuestDto() throws RepeatedElementException, InvalidElementException {
        // Arrange
        when(guestRepository.existsByEmail(validCreateGuestDto.email())).thenReturn(false);
        when(guestRepository.existsByPhoneNumber(validCreateGuestDto.phoneNumber())).thenReturn(false);
        when(guestMapper.toEntity(validCreateGuestDto)).thenReturn(validGuest);
        when(passwordEncoder.encode(validCreateGuestDto.password())).thenReturn("encodedPassword123");
        when(guestRepository.save(any(Guest.class))).thenReturn(validGuest);
        when(guestMapper.toDto(validGuest)).thenReturn(validGuestDto);
        doNothing().when(emailService).sendWelcomeEmail(anyString(), any(GuestDto.class));

        // Act
        GuestDto result = guestService.registerGuest(validCreateGuestDto);

        // Assert
        assertNotNull(result);
        assertEquals(validGuestDto.id(), result.id());
        assertEquals(validGuestDto.email(), result.email());
        assertEquals(validGuestDto.name(), result.name());
        assertEquals(StatesOfGuest.ACTIVE, validGuest.getState());
        
        verify(guestRepository).existsByEmail(validCreateGuestDto.email());
        verify(guestRepository).existsByPhoneNumber(validCreateGuestDto.phoneNumber());
        verify(guestMapper).toEntity(validCreateGuestDto);
        verify(passwordEncoder).encode(validCreateGuestDto.password());
        verify(guestRepository).save(any(Guest.class));
        verify(emailService).sendWelcomeEmail(validGuest.getEmail(), result);
    }

    /**
     * Test 2: Fracaso - Email ya registrado
     */
    @Test
    void registerGuest_EmailAlreadyExists_ShouldThrowRepeatedElementException() {
        // Arrange
        when(guestRepository.existsByEmail(validCreateGuestDto.email())).thenReturn(true);

        // Act & Assert
        RepeatedElementException exception = assertThrows(
                RepeatedElementException.class,
                () -> guestService.registerGuest(validCreateGuestDto)
        );

        assertEquals("El correo electronico ya esta registrado", exception.getMessage());
        verify(guestRepository).existsByEmail(validCreateGuestDto.email());
        verify(guestRepository, never()).existsByPhoneNumber(anyString());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 3: Fracaso - Número de teléfono ya registrado
     */
    @Test
    void registerGuest_PhoneNumberAlreadyExists_ShouldThrowRepeatedElementException() {
        // Arrange
        when(guestRepository.existsByEmail(validCreateGuestDto.email())).thenReturn(false);
        when(guestRepository.existsByPhoneNumber(validCreateGuestDto.phoneNumber())).thenReturn(true);

        // Act & Assert
        RepeatedElementException exception = assertThrows(
                RepeatedElementException.class,
                () -> guestService.registerGuest(validCreateGuestDto)
        );

        assertEquals("El teléfono ya está registrado", exception.getMessage());
        verify(guestRepository).existsByEmail(validCreateGuestDto.email());
        verify(guestRepository).existsByPhoneNumber(validCreateGuestDto.phoneNumber());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 4: Datos inválidos - Fecha de nacimiento en el futuro
     */
    @Test
    void registerGuest_FutureBirthDate_ShouldThrowInvalidElementException() {
        // Arrange
        CreateGuestDto invalidDto = new CreateGuestDto(
                "Juan Pérez",
                "+573001234567",
                LocalDate.now().plusDays(1), // Fecha futura
                "juan.perez@email.com",
                "Password123",
                Role.GUEST,
                null
        );

        when(guestRepository.existsByEmail(invalidDto.email())).thenReturn(false);
        when(guestRepository.existsByPhoneNumber(invalidDto.phoneNumber())).thenReturn(false);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.registerGuest(invalidDto)
        );

        assertEquals("Fecha nacimiento es invalida", exception.getMessage());
        verify(guestRepository).existsByEmail(invalidDto.email());
        verify(guestRepository).existsByPhoneNumber(invalidDto.phoneNumber());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 5: Datos inválidos - Usuario menor de edad
     */
    @Test
    void registerGuest_UnderageUser_ShouldThrowInvalidElementException() {
        // Arrange
        CreateGuestDto underageDto = new CreateGuestDto(
                "Juan Pérez",
                "+573001234567",
                LocalDate.now().minusYears(17), // Menor de edad
                "juan.perez@email.com",
                "Password123",
                Role.GUEST,
                null
        );

        when(guestRepository.existsByEmail(underageDto.email())).thenReturn(false);
        when(guestRepository.existsByPhoneNumber(underageDto.phoneNumber())).thenReturn(false);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.registerGuest(underageDto)
        );

        assertEquals("Eres menor de edad, lo lamentamos ", exception.getMessage());
        verify(guestRepository).existsByEmail(underageDto.email());
        verify(guestRepository).existsByPhoneNumber(underageDto.phoneNumber());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 6: Datos inválidos - Rol nulo
     */
    @Test
    void registerGuest_NullRole_ShouldThrowInvalidElementException() {
        // Arrange
        CreateGuestDto nullRoleDto = new CreateGuestDto(
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                "juan.perez@email.com",
                "Password123",
                null, // Rol nulo
                null
        );

        when(guestRepository.existsByEmail(nullRoleDto.email())).thenReturn(false);
        when(guestRepository.existsByPhoneNumber(nullRoleDto.phoneNumber())).thenReturn(false);

        // Act & Assert
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> guestService.registerGuest(nullRoleDto)
        );

        assertEquals("El rol es obligatorio", exception.getMessage());
        verify(guestRepository).existsByEmail(nullRoleDto.email());
        verify(guestRepository).existsByPhoneNumber(nullRoleDto.phoneNumber());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    /**
     * Test 7: Caso edge - Registro exitoso con foto de perfil
     */
    @Test
    void registerGuest_SuccessWithProfilePhoto_ShouldReturnGuestDto() throws RepeatedElementException, InvalidElementException {
        // Arrange
        MultipartFile profilePhoto = new MockMultipartFile(
                "profilePhoto", 
                "profile.jpg", 
                "image/jpeg", 
                "test image content".getBytes()
        );

        CreateGuestDto dtoWithPhoto = new CreateGuestDto(
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                "juan.perez@email.com",
                "Password123",
                Role.GUEST,
                profilePhoto
        );

        Guest guestWithPhoto = new Guest();
        guestWithPhoto.setId(1L);
        guestWithPhoto.setName("Juan Pérez");
        guestWithPhoto.setEmail("juan.perez@email.com");
        guestWithPhoto.setPhoneNumber("+573001234567");
        guestWithPhoto.setBirthDate(LocalDate.of(1995, 5, 15));
        guestWithPhoto.setRole(Role.GUEST);
        guestWithPhoto.setState(StatesOfGuest.ACTIVE);
        guestWithPhoto.setUrlProfilePhoto("https://cloudinary.com/image123");

        GuestDto guestDtoWithPhoto = new GuestDto(
                1L,
                "juan.perez@email.com",
                "juan.perez",
                "Juan Pérez",
                "+573001234567",
                LocalDate.of(1995, 5, 15),
                profilePhoto,
                StatesOfGuest.ACTIVE,
                Role.GUEST,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(guestRepository.existsByEmail(dtoWithPhoto.email())).thenReturn(false);
        when(guestRepository.existsByPhoneNumber(dtoWithPhoto.phoneNumber())).thenReturn(false);
        when(guestMapper.toEntity(dtoWithPhoto)).thenReturn(guestWithPhoto);
        when(passwordEncoder.encode(dtoWithPhoto.password())).thenReturn("encodedPassword123");
        when(cloudinaryService.uploadPhoto(profilePhoto)).thenReturn("https://cloudinary.com/image123");
        when(guestRepository.save(any(Guest.class))).thenReturn(guestWithPhoto);
        when(guestMapper.toDto(guestWithPhoto)).thenReturn(guestDtoWithPhoto);
        doNothing().when(emailService).sendWelcomeEmail(anyString(), any(GuestDto.class));

        // Act
        GuestDto result = guestService.registerGuest(dtoWithPhoto);

        // Assert
        assertNotNull(result);
        assertEquals(guestDtoWithPhoto.id(), result.id());
        assertEquals(guestDtoWithPhoto.email(), result.email());
        assertEquals("https://cloudinary.com/image123", guestWithPhoto.getUrlProfilePhoto());
        
        verify(guestRepository).existsByEmail(dtoWithPhoto.email());
        verify(guestRepository).existsByPhoneNumber(dtoWithPhoto.phoneNumber());
        verify(guestMapper).toEntity(dtoWithPhoto);
        verify(passwordEncoder).encode(dtoWithPhoto.password());
        verify(cloudinaryService).uploadPhoto(profilePhoto);
        verify(guestRepository).save(any(Guest.class));
        verify(emailService).sendWelcomeEmail(guestWithPhoto.getEmail(), result);
    }
}
