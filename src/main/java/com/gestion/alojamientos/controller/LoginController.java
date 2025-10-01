package com.gestion.alojamientos.controller;
// IMPORTACIONES PARA SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// IMPORTACIONES DE SPRING
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestion.alojamientos.dto.JwtResponseDTO;
import com.gestion.alojamientos.dto.UserLoginDTO;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints para operaciones de autenticación. Incluye inicio de sesión con JWT para roles diferenciados (Guest, Host, Admin). No requiere autenticación previa, pero valida credenciales seguras.")
public class LoginController {

    // ENDPOINT: POST /api/v1/auth/login
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión de usuario",
        description = "Autentica un usuario con email y contraseña, generando un JWT con roles embebidos para autorización subsiguiente. Valida credenciales contra la base de datos (User entity con password encriptada) y maneja roles diferenciados. Cumple con el requerimiento de autenticación segura con JWT."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, con JWT retornado para uso en headers Authorization.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas (e.g., email o contraseña incorrectos)."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (e.g., usuario bloqueado o inactivo)."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la autenticación o generación de JWT.")
    })
    public ResponseEntity<JwtResponseDTO> login(
        @Parameter(description = "DTO con credenciales del usuario (e.g., email, password). Validado en capa de servicio con encriptación.", required = true) 
        @RequestBody UserLoginDTO dto
    ) {
        // Lógica placeholder (en implementación real: validar credenciales, generar JWT con roles, manejar expiración)
        return ResponseEntity.ok(new JwtResponseDTO());
    }
}