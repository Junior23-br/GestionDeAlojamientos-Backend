package com.gestion.alojamientos.resources.test;

import com.gestion.alojamientos.dto.Host.HostCreateDTO;
import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.mapper.users.HostMapper;
import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.HostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para el método registerHost del servicio HostServiceImpl.
 * Prueba la funcionalidad de registrar un nuevo host en el sistema.
 */
@ExtendWith(MockitoExtension.class)
class HostServiceRegisterHostTest {

    @Mock
    private HostRepo hostRepository;

    @Mock
    private HostMapper hostMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private HostServiceImpl hostService;

    private HostCreateDTO validHostCreateDTO;
    private HostCreateDTO duplicateEmailHostCreateDTO;
    private HostCreateDTO futureBirthDateHostCreateDTO;
    private HostCreateDTO minorAgeHostCreateDTO;
    private Host host;
    private HostDTO hostDTO;

    @BeforeEach
    void setUp() {
        validHostCreateDTO = new HostCreateDTO(
                "Juan Pérez",                    // name
                "+571234567890",                 // phoneNumber
                LocalDate.of(1990, 5, 15),      // birthDate (adult)
                "juan@test.com",                 // email
                "Soy un anfitrión experimentado", // personalDescription
                "Password123",                   // password
                Role.HOST                        // role
        );

        duplicateEmailHostCreateDTO = new HostCreateDTO(
                "María García",                  // name
                "+579876543210",                 // phoneNumber
                LocalDate.of(1985, 8, 20),      // birthDate
                "juan@test.com",                 // email (duplicate)
                "Descripción personal",          // personalDescription
                "Password456",                   // password
                Role.HOST                        // role
        );

        futureBirthDateHostCreateDTO = new HostCreateDTO(
                "Ana López",                     // name
                "+571112223334",                 // phoneNumber
                LocalDate.now().plusDays(1),    // birthDate (future date)
                "ana@test.com",                  // email
                "Descripción personal",          // personalDescription
                "Password789",                   // password
                Role.HOST                        // role
        );

        minorAgeHostCreateDTO = new HostCreateDTO(
                "Carlos Niño",                   // name
                "+575556667778",                 // phoneNumber
                LocalDate.now().minusYears(16), // birthDate (minor)
                "carlos@test.com",               // email
                "Descripción personal",          // personalDescription
                "Password101",                   // password
                Role.HOST                        // role
        );

        host = new Host();
        host.setId(1L);
        host.setEmail("juan@test.com");
        host.setUsername("juan@test.com");
        host.setName("Juan Pérez");
        host.setPhoneNumber("+571234567890");
        host.setBirthDate(new Date());
        host.setPersonalDescription("Soy un anfitrión experimentado");
        host.setStatus(StatesOfHost.ACTIVE);
        host.setRole(Role.HOST);

        hostDTO = new HostDTO(
                1L,                             // id
                "juan@test.com",                // email
                "juan@test.com",                // username
                "Juan Pérez",                   // name
                "+571234567890",                // phoneNumber
                new Date(),                     // birthDate
                null,                           // urlProfilePhoto
                StatesOfHost.ACTIVE,            // status
                "Soy un anfitrión experimentado", // personalDescription
                List.of(),                      // listAccommodationsIds
                List.of(),                      // hostCommentIds
                null,                           // financialAccountId
                null,                           // serviceFeeId
                Role.HOST                       // role
        );
    }

    /**
     * Prueba el caso de éxito: registrar un host con datos válidos.
     * Verifica que se registre correctamente el host y se retorne el HostDTO.
     */
    @Test
    void shouldReturnHostDTO_WhenRegistrationIsSuccessful() throws RepeatedElementException, InvalidElementException {
        // Given
        when(hostRepository.existsByEmail(validHostCreateDTO.email())).thenReturn(false);
        when(hostMapper.toEntity(validHostCreateDTO)).thenReturn(host);
        when(passwordEncoder.encode(validHostCreateDTO.password())).thenReturn("encodedPassword");
        when(hostRepository.save(any(Host.class))).thenReturn(host);
        when(hostMapper.toDTO(host)).thenReturn(hostDTO);

        // When
        HostDTO result = hostService.registerHost(validHostCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("juan@test.com", result.email());
        assertEquals("Juan Pérez", result.name());
        assertEquals(StatesOfHost.ACTIVE, result.status());
        verify(hostRepository).existsByEmail(validHostCreateDTO.email());
        verify(hostMapper).toEntity(validHostCreateDTO);
        verify(passwordEncoder).encode(validHostCreateDTO.password());
        verify(hostRepository).save(host);
        verify(hostMapper).toDTO(host);
    }

    /**
     * Prueba el caso de fracaso: cuando el email ya está registrado.
     * Verifica que se lance RepeatedElementException cuando el email ya existe.
     */
    @Test
    void shouldThrowRepeatedElementException_WhenEmailAlreadyExists() {
        // Given
        when(hostRepository.existsByEmail(duplicateEmailHostCreateDTO.email())).thenReturn(true);

        // When & Then
        RepeatedElementException exception = assertThrows(
                RepeatedElementException.class,
                () -> hostService.registerHost(duplicateEmailHostCreateDTO)
        );

        assertEquals("El correo electrónico ya está registrado.", exception.getMessage());
        verify(hostRepository).existsByEmail(duplicateEmailHostCreateDTO.email());
        verifyNoInteractions(hostMapper);
        verifyNoInteractions(passwordEncoder);
    }

    /**
     * Prueba el caso de datos inválidos: fecha de nacimiento futura.
     * Verifica que se lance InvalidElementException cuando la fecha de nacimiento es futura.
     */
    @Test
    void shouldHandleInvalidData_WhenBirthDateIsInTheFuture() {
        // Given
        when(hostRepository.existsByEmail(futureBirthDateHostCreateDTO.email())).thenReturn(false);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> hostService.registerHost(futureBirthDateHostCreateDTO)
        );

        assertEquals("La fecha de nacimiento es inválida.", exception.getMessage());
        verify(hostRepository).existsByEmail(futureBirthDateHostCreateDTO.email());
        verifyNoInteractions(hostMapper);
        verifyNoInteractions(passwordEncoder);
    }

    /**
     * Prueba el caso edge: cuando el usuario es menor de edad.
     * Verifica que se lance InvalidElementException cuando el usuario no es mayor de edad.
     */
    @Test
    void shouldHandleEdgeCase_WhenUserIsMinor() {
        // Given
        when(hostRepository.existsByEmail(minorAgeHostCreateDTO.email())).thenReturn(false);

        // When & Then
        InvalidElementException exception = assertThrows(
                InvalidElementException.class,
                () -> hostService.registerHost(minorAgeHostCreateDTO)
        );

        assertEquals("Debes ser mayor de edad para registrarte como Host.", exception.getMessage());
        verify(hostRepository).existsByEmail(minorAgeHostCreateDTO.email());
        verifyNoInteractions(hostMapper);
        verifyNoInteractions(passwordEncoder);
    }
}
