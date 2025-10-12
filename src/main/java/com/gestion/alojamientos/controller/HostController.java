package com.gestion.alojamientos.controller;
import com.gestion.alojamientos.dto.JwtResponseDTO;
import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.Host.*;
import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.dto.password.GenerateResetCodeDto;
import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.security.jwt.JwtUtil;
import com.gestion.alojamientos.service.HostService;
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

@RestController
@RequestMapping("/api/hosts")
@Tag(name = "Hosts", description = "Endpoints para la gestión y autenticación de anfitriones del sistema.")
public class HostController {

    @Autowired
    private HostService hostService;

    @Autowired
    private JwtUtil jwtUtil;

    // --------------------------
    // REGISTER HOST
    // --------------------------
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo Host", description = "Registra un nuevo anfitrión en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Host registrado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HostDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario menor de edad."),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<HostDTO> registerHost(@Valid @RequestBody HostCreateDTO dto) {
        try {
            HostDTO creado = hostService.registerHost(dto);
            return ResponseEntity.status(201).body(creado);
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (RepeatedElementException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // LOGIN HOST
    // --------------------------
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión Host", description = "Autentica un anfitrión y devuelve un token JWT.")
    public ResponseEntity<JwtResponseDTO> loginHost(@Valid @RequestBody UserLoginDTO dto) {
        try {
            HostDTO host = hostService.loginHost(dto);

            String token = jwtUtil.generateToken(
                    host.email(),
                    host.id(),
                    "HOST"
            );

            JwtResponseDTO response = new JwtResponseDTO(
                    token,
                    "Bearer",
                    "HOST",
                    host.id(),
                    host.email()
            );

            return ResponseEntity.ok(response);
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // GET HOST BY ID
    // --------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Obtener Host por ID", description = "Devuelve la información de un anfitrión específico por su ID.")
    public ResponseEntity<HostDTO> getHostById(
            @Parameter(description = "ID del Host a consultar.", required = true)
            @PathVariable Long id
    ) {
        try {
            HostDTO host = hostService.getHostById(id);
            return ResponseEntity.ok(host);
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // UPDATE HOST
    // --------------------------
    @PutMapping("/{id}")
    @Operation(summary = "Editar Host", description = "Actualiza la información de un anfitrión existente.")
    public ResponseEntity<HostDTO> updateHost(
            @PathVariable Long id,
            @Valid @RequestBody HostUpdateDTO dto
    ) {
        try {
            HostDTO actualizado = hostService.editHost(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // DELETE HOST
    // --------------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Host", description = "Elimina lógicamente un anfitrión cambiando su estado a 'DELETED'.")
    public ResponseEntity<Void> deleteHost(
            @PathVariable Long id,
            @Valid @RequestBody DeleteHostDTO dto
    ) {
        try {
            hostService.deleteHost(id, dto);
            return ResponseEntity.noContent().build();
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // RESET PASSWORD - NO AUTHENTICATED 
    // --------------------------
    @PostMapping("/generate-reset-code")
    @Operation(summary = "Restablecer contraseña", description = "Permite restablecer la contraseña del Host mediante un código de verificación.")
    public ResponseEntity<?> generateResetCode(@Valid @RequestBody GenerateResetCodeDto dto) {
        try {
            hostService.generateResetCode(dto.email());
            return ResponseEntity.ok().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

     // --------------------------
    // CHANGE PASSWORD - NO AUTHENTICATED
    // --------------------------
    @PostMapping("/reset-password")
    @Operation(summary = "Cambiar contraseña", description = "Permite restablecer la contraseña del Host mediante un código de verificación usando el codigo de verificacion proporcionado al email por resetPasswword.")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordDto dto
    ) {
        try {
            hostService.resetPassword(dto);
            return ResponseEntity.ok().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // CHANGE PASSWORD - AUTHENTICATED
    // --------------------------
    @PutMapping("/change-password/{id}")
    @Operation(summary = "Cambiar contraseña", description = "Permite al Host cambiar su contraseña actual por una nueva.")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDto dto
    ) {
        try {
            hostService.changePassword(id, dto);
            return ResponseEntity.ok().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
