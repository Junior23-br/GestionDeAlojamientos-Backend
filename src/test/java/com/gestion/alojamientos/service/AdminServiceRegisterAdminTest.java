package com.gestion.alojamientos.service;
import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.dto.admin.CreateAdminDto;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.mapper.users.AdminMapper;
import com.gestion.alojamientos.model.enums.StatesAdmin;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.repository.user.AdminRepository;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceRegisterAdminTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private CreateAdminDto validDto;
    private Admin savedAdmin;
    private AdminDto mappedDto;

    @BeforeEach
    void setUp() {
        validDto = new CreateAdminDto("admin@email.com", "password123");

        savedAdmin = new Admin();
        savedAdmin.setId(1L);
        savedAdmin.setEmail(validDto.email());
        savedAdmin.setUsername("admin");
        savedAdmin.setPassword("encoded-password");
        savedAdmin.setAccess_level(1);
        savedAdmin.setStatesAdmin(StatesAdmin.ACTIVE);

        mappedDto = new AdminDto(
                1L,
                savedAdmin.getEmail(),
                savedAdmin.getUsername(),
                savedAdmin.getAccess_level(),
                savedAdmin.getStatesAdmin().name(),
                null
        );

    }

    /**
     * Caso 1: ÉXITO - Registro exitoso de administrador
     */
    @Test
    void registerAdmin_Success_ShouldSaveAdminAndReturnDto() throws RepeatedElementException {
        // Arrange
        when(adminRepository.existsByEmail(validDto.email())).thenReturn(false);
        when(passwordEncoder.encode(validDto.password())).thenReturn("encoded-password");
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(adminMapper.toDTO(savedAdmin)).thenReturn(mappedDto);

        // Act
        AdminDto result = adminService.registerAdmin(validDto);

        // Assert
        assertNotNull(result);
        assertEquals("admin@email.com", result.email());
        assertEquals("admin", result.username());
        assertEquals(1, result.access_level());
        verify(adminRepository).existsByEmail(validDto.email());
        verify(passwordEncoder).encode(validDto.password());
        verify(adminRepository).save(any(Admin.class));
        verify(adminMapper).toDTO(savedAdmin);
    }

    /**
     * Caso 2: VALIDACIÓN - Ya existe un admin con el mismo email
     */
    @Test
    void registerAdmin_DuplicateEmail_ShouldThrowRepeatedElementException() {
        // Arrange
        when(adminRepository.existsByEmail(validDto.email())).thenReturn(true);

        // Act & Assert
        RepeatedElementException exception = assertThrows(
                RepeatedElementException.class,
                () -> adminService.registerAdmin(validDto)
        );

        assertEquals("Ya existe un administrador con el correo proporcionado.", exception.getMessage());
        verify(adminRepository).existsByEmail(validDto.email());
        verify(adminRepository, never()).save(any(Admin.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    /**
     * Caso 3: EDGE CASES - Variaciones en datos válidos pero atípicos
     */
    @Test
    void registerAdmin_WithoutUsername_ShouldDeriveUsernameFromEmail() throws RepeatedElementException {
        // Arrange
        CreateAdminDto dto = new CreateAdminDto("john.doe@email.com", "mypassword");
        when(adminRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded");
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> {
            Admin admin = invocation.getArgument(0);
            admin.setId(99L);
            return admin;
        });
        when(adminMapper.toDTO(any(Admin.class))).thenReturn(mappedDto);

        // Act
        AdminDto result = adminService.registerAdmin(dto);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@email.com", dto.email());
        verify(adminRepository).existsByEmail(dto.email());
        verify(passwordEncoder).encode(dto.password());
        verify(adminRepository).save(any(Admin.class));
        verify(adminMapper).toDTO(any(Admin.class));
    }

    /**
     * Caso 4: ERROR INTERNO - Error inesperado al guardar el admin
     */
    @Test
    void registerAdmin_InternalError_ShouldThrowRuntimeException() {
        // Arrange
        when(adminRepository.existsByEmail(validDto.email())).thenReturn(false);
        when(passwordEncoder.encode(validDto.password())).thenReturn("encoded-password");
        when(adminRepository.save(any(Admin.class))).thenThrow(new RuntimeException("Database failure"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminService.registerAdmin(validDto)
        );

        assertTrue(exception.getMessage().contains("Database failure"));
        verify(adminRepository).existsByEmail(validDto.email());
        verify(passwordEncoder).encode(validDto.password());
        verify(adminRepository).save(any(Admin.class));
    }
}
