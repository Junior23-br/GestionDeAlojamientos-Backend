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
import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.dto.Host.HostCreateDTO;
import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.security.jwt.JwtUtil;
import com.gestion.alojamientos.service.GuestService;
import com.gestion.alojamientos.service.HostService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints para operaciones de autenticación. Incluye inicio de sesión para roles diferenciados (Guest, Host). No requiere autenticación previa, pero valida credenciales seguras.")
public class AuthController {
    
    @Autowired
    private GuestService guestService;
    @Autowired
    private HostService hostService;
    @Autowired
    private JwtUtil jwtUtil;

    // POST ENDPOINT: /api/auth/guest/login - FOR GUEST

    @PostMapping("/guest/login")
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
    public ResponseEntity<JwtResponseDTO> loginGuest(
            @Parameter(description = "DTO con credenciales del usuario (email, password). Validado en capa de servicio con encriptación.", required = true)
            @Valid @RequestBody UserLoginDTO dto
    ) {
        JwtResponseDTO resp;
        try {
            GuestDto guestDto = guestService.login(dto);
            String token = jwtUtil.generateToken(guestDto.email(), guestDto.id(), guestDto.role().name());
            resp = new JwtResponseDTO(token, "Bearer", guestDto.role().name(), guestDto.id(), guestDto.email());
        } catch (InvalidElementException e) {
            // System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
        return ResponseEntity.ok(resp);
    }

    // ENDPOINT: POST /api/auth/guest/signup
    @PostMapping("/guest/signup")
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
    public ResponseEntity<GuestDto> signUpGuest(
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

    // ENDPOINT: POST /api/auth/guest/signup
    @PostMapping("/host/signup")
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
    public ResponseEntity<HostDTO> signUpHost(
        @Parameter(description = "DTO con datos del usuario (e.g., email, password, name, birth_date, role opcional). Validado en capa de servicio con encriptación.", required = true) 
        @RequestBody HostCreateDTO dto
    ) {
        HostDTO host;
        try {
            host = hostService.registerHost(dto);
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
        return ResponseEntity.status(201).body(host);
    }

    // ENDPOINT: POST /api/auth/host/login - FOR HOST

    @PostMapping("host/login")
    @Operation(
            summary = "Iniciar sesión de usuario de tipo Host",
            description = "Autentica un usuario con email y contraseña, devolviendo los datos del huésped autenticado. Valida credenciales contra la base de datos (Guest entity con password encriptada) y maneja roles diferenciados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, con datos del host retornados.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HostDTO.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas (e.g., email o contraseña incorrectos)."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (e.g., usuario eliminado o suspendido)."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la autenticación.")
    })
    public ResponseEntity<JwtResponseDTO> loginHost(
            @Parameter(description = "DTO con credenciales del usuario (email, password). Validado en capa de servicio con encriptación.", required = true)
            @Valid @RequestBody UserLoginDTO dto
    ) {
        HostDTO host;
        JwtResponseDTO resp;
        try {
            host = hostService.loginHost(dto);
            String token = jwtUtil.generateToken(host.email(), host.id(), host.role().name());
            resp = new JwtResponseDTO(token, "Bearer", host.role().name(), host.id(), host.email());
        } catch (InvalidElementException e) {
            // System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
        return ResponseEntity.ok(resp);
    }


}
