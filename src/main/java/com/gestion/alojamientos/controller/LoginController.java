package com.gestion.alojamientos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.service.GuestService;

// @RestController
// @RequestMapping("/api/v1/auth")
// @Tag(name = "Authentication", description = "Endpoints para operaciones de autenticación. Incluye inicio de sesión para roles diferenciados (Guest, Host). No requiere autenticación previa, pero valida credenciales seguras.")
// public class LoginController {

//     @Autowired
//     private GuestService guestService;

//     @PostMapping("/login")
//     @Operation(
//             summary = "Iniciar sesión de usuario",
//             description = "Autentica un usuario con email y contraseña, devolviendo los datos del huésped autenticado. Valida credenciales contra la base de datos (Guest entity con password encriptada) y maneja roles diferenciados."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, con datos del huésped retornados.",
//                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuestDto.class))),
//             @ApiResponse(responseCode = "400", description = "Credenciales inválidas (e.g., email o contraseña incorrectos)."),
//             @ApiResponse(responseCode = "403", description = "Acceso denegado (e.g., usuario eliminado o suspendido)."),
//             @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la autenticación.")
//     })
//     public ResponseEntity<GuestDto> login(
//             @Parameter(description = "DTO con credenciales del usuario (email, password). Validado en capa de servicio con encriptación.", required = true)
//             @Valid @RequestBody UserLoginDTO dto
//     ) {
//         try {
//             GuestDto guestDto = guestService.login(dto);
//             return ResponseEntity.ok(guestDto);
//         } catch (InvalidElementException e) {
//             return ResponseEntity.badRequest().body(null);
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body(null);
//         }
//     }

//     @PostMapping("/signup2")
//     private ResponseEntity signUp() {
//         // Lógica de registro de usuario
//         return ResponseEntity.status(200).body(null);
//     }

// }