package com.gestion.alojamientos.controller.Admin;

import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.dto.admin.CreateAdminDto;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.repository.AdminRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/auth")
@Tag(name = "Admin Authentication", description = "Endpoints de autenticación y registro para administradores")
public class AdminAuthController {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AdminAuthController(AdminRepository adminRepository,
                               AdminMapper adminMapper,
                               PasswordEncoder passwordEncoder,
                               AuthenticationManager authenticationManager) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    // ---------- LOGIN ----------
    @PostMapping("/login")
    @Operation(
            summary = "Login de administrador",
            description = "Valida email y contraseña de un admin. (Por ahora retorna DTO, en el futuro JWT)."
    )
    @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminDto.class)))
    public ResponseEntity<AdminDto> login(@RequestParam String email, @RequestParam String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        return ResponseEntity.ok(adminMapper.toDto(admin));
    }

    // ---------- REGISTRO ----------
    @PostMapping("/register")
    @Operation(
            summary = "Registro de administrador",
            description = "Crea un nuevo administrador en el sistema con email único."
    )
    @ApiResponse(responseCode = "201", description = "Admin creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminDto.class)))
    public ResponseEntity<AdminDto> register(@Valid @RequestBody CreateAdminDto dto) {
        if (adminRepository.existsByEmail(dto.email())) {
            return ResponseEntity.badRequest().build();
        }

        Admin admin = new Admin();
        admin.setEmail(dto.email());
        admin.setPassword(passwordEncoder.encode(dto.password()));

        Admin saved = adminRepository.save(admin);
        return ResponseEntity.status(201).body(adminMapper.toDto(saved));
    }
}
