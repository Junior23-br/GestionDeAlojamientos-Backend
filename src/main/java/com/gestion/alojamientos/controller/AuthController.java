package com.gestion.alojamientos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestion.alojamientos.dto.JwtResponseDTO;
import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.UserSignUpDTO;
import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.service.GuestService;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints para operaciones de autenticación. Incluye inicio de sesión para roles diferenciados (Guest, Host). No requiere autenticación previa, pero valida credenciales seguras.")
public class AuthController {
    
    @Autowired
    private GuestService guestService;

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión de usuario",
            description = "Autentica un usuario con email y contraseña, devolviendo los datos del huésped autenticado. Valida credenciales contra la base de datos (Guest entity con password encriptada) y maneja roles diferenciados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, con datos del huésped retornados.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuestDto.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas (e.g., email o contraseña incorrectos)."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (e.g., usuario eliminado o suspendido)."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la autenticación.")
    })
    public ResponseEntity<GuestDto> login(
            @Parameter(description = "DTO con credenciales del usuario (email, password). Validado en capa de servicio con encriptación.", required = true)
            @Valid @RequestBody UserLoginDTO dto
    ) {
        GuestDto guestDto;
        try {
            guestDto = guestService.login(dto);
        } catch (InvalidElementException e) {
            // System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
        return ResponseEntity.ok(guestDto);
    }

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
    public ResponseEntity<GuestDto> signUp(
        @Parameter(description = "DTO con datos del usuario (e.g., email, password, name, birth_date, role opcional). Validado en capa de servicio con encriptación.", required = true) 
        @RequestBody CreateGuestDto dto
    ) {

        GuestDto creado = null;
        try {
            creado = guestService.registerGuest(dto);
        } catch (InvalidElementException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);   
        }
        catch (RepeatedElementException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(409).body(null);   
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
        // Lógica placeholder (en implementación real: validar datos, encriptar password, persistir User, generar JWT, notificar si aplica)
        return ResponseEntity.status(201).body(creado);
    }

}
