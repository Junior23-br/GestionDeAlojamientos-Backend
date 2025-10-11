package com.gestion.alojamientos.controller.Admin;


import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.admin.*;
import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.mapper.UserLoginMapper;
import com.gestion.alojamientos.service.AdminService;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
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

/**
 * Controlador REST para la gestión de administradores del sistema.
 * Incluye operaciones CRUD, autenticación, cambio y restablecimiento de contraseñas.
 */
@RestController
@RequestMapping("/api/admins")
@Tag(name = "Administradores", description = "Endpoints para gestión y autenticación de administradores del sistema. Permite registrar, editar, eliminar, autenticar y administrar contraseñas.")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminServiceImpl adminServiceImpl;


    // --------------------------
    // CREATE ADMIN
    // --------------------------
    @PostMapping
    @Operation(summary = "Registrar nuevo administrador",
            description = "Crea un nuevo administrador validando unicidad del email y estructura de los datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrador creado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o estructura incorrecta."),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<AdminDto> createAdmin(
            @Parameter(description = "Datos del nuevo administrador a registrar.", required = true)
            @Valid @RequestBody CreateAdminDto dto
    ) {
        try {
            AdminDto creado = adminService.registerAdmin(dto);
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
    // GET ADMIN BY ID
    // --------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Obtener administrador por ID",
            description = "Devuelve los datos de un administrador específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminDto.class))),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<AdminDto> getAdminById(
            @Parameter(description = "ID del administrador a consultar.", required = true)
            @PathVariable String id
    ) {
        try {
            AdminDto admin = adminService.getAdminById(Long.valueOf(id));
            return ResponseEntity.ok(admin);
        } catch (InvalidElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // GET ADMIN BY EMAIL
    // --------------------------
    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener administrador por email",
            description = "Busca un administrador a partir de su dirección de correo electrónico.")
    public ResponseEntity<AdminDto> getAdminByEmail(
            @Parameter(description = "Email del administrador.", required = true)
            @PathVariable String email
    ) {
        try {
            AdminDto admin = adminService.getAdminByEmail(email);
            return ResponseEntity.ok(admin);
        } catch (InvalidElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // UPDATE ADMIN
    // --------------------------
    @PutMapping("/{id}")
    @Operation(summary = "Editar datos del administrador",
            description = "Permite actualizar nombre, email u otros datos del administrador.")
    public ResponseEntity<AdminDto> updateAdmin(
            @Parameter(description = "ID del administrador a editar.", required = true)
            @PathVariable String id,
            @Valid @RequestBody EditAdminDto dto
    ) {
        try {
            AdminDto actualizado = adminService.editAdmin(Long.valueOf(id), dto);
            return ResponseEntity.ok(actualizado);
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // DELETE ADMIN
    // --------------------------
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar administrador",
            description = "Elimina un administrador del sistema de forma permanente.")
    public ResponseEntity<Void> deleteAdmin(
            @Parameter(description = "ID del administrador a eliminar.", required = true)
            @PathVariable String id
    ) {
        try {
            adminService.deleteAdmin(Long.valueOf(id));
            return ResponseEntity.noContent().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // LOGIN ADMIN
    // --------------------------
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión como administrador",
            description = "Valida las credenciales del administrador y devuelve sus datos si son correctos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminDto.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<UserLoginDTO> loginAdmin(
            @Parameter(description = "Credenciales del administrador (email, password).", required = true)
            @RequestBody UserLoginDTO dto
    ) {
        try {
            UserLoginDTO admin = adminServiceImpl.login(dto);

            return ResponseEntity.ok(admin);
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // RESET PASSWORD
    // --------------------------
    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña del administrador",
            description = "Permite generar una nueva contraseña temporal y enviarla al correo del administrador.")
    public ResponseEntity<?> resetPassword(
            @Parameter(description = "Email del administrador al que se le restablecerá la contraseña.", required = true)
            @RequestBody ResetPasswordDto dto
    ) {
        try {
            adminService.resetPassword(dto);
            return ResponseEntity.ok().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // CHANGE PASSWORD
    // --------------------------
    @PostMapping("/change-password")
    @Operation(summary = "Cambiar contraseña del administrador",
            description = "Permite al administrador autenticado cambiar su contraseña actual por una nueva.")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "Datos del cambio de contraseña (email, contraseña actual y nueva).", required = true)
            @RequestBody ChangePasswordDto dto, Long idUser
    ) {
        try {
            adminService.changePassword(idUser, dto);
            return ResponseEntity.ok().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}