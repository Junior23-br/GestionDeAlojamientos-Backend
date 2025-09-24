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
import com.gestion.alojamientos.dto.UserSignUpDTO;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints para operaciones de autenticación y registro. Incluye registro de nuevos usuarios (Guest o Host) con validaciones y generación de JWT. No requiere autenticación previa.")
public class SignUpController {

    // ENDPOINT: POST /api/v1/auth/signup
    @PostMapping("/signup")
    @Operation(
        summary = "Registrar un nuevo usuario",
        description = "Registra un nuevo usuario (Guest por defecto, Host si aplica) con validaciones de email único, edad mínima (18 años), y contraseña segura. Encripta la contraseña y genera un JWT con rol embebido para acceso inmediato. Puede incluir verificación por email como paso opcional."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente, con JWT retornado para uso en headers Authorization.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Validación fallida (e.g., edad < 18, contraseña débil, datos incompletos)."),
        @ApiResponse(responseCode = "409", description = "Email ya registrado (conflicto de unicidad)."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor durante el registro o generación de JWT.")
    })
    public ResponseEntity<JwtResponseDTO> signUp(
        @Parameter(description = "DTO con datos del usuario (e.g., email, password, name, birth_date, role opcional). Validado en capa de servicio con encriptación.", required = true) 
        @RequestBody UserSignUpDTO dto
    ) {
        // Lógica placeholder (en implementación real: validar datos, encriptar password, persistir User, generar JWT, notificar si aplica)
        return ResponseEntity.status(201).body(new JwtResponseDTO());
    }
}