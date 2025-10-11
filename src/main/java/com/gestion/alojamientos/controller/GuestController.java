package com.gestion.alojamientos.controller;
import com.gestion.alojamientos.dto.JwtResponseDTO;
import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.DeleteGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.dto.password.GenerateResetCodeDto;
import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.security.jwt.JwtUtil;
import com.gestion.alojamientos.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Será el controlador REST el cual gestionara las operaciones relacionadas
 * con huespedes. Además proporciona endpoints para poder registrar, eliminar editar y consultar huespedes.
 */

@RestController
@RequestMapping("/api/guests")
@Tag(name = "Guest", description = "Endpoints para la gestión de operaciones de huespedes. Incluye la creación de huespedes, la eliminación de perfiles de huespedes, buscarlos por ID y tambie´n permite obtener los detalles de un huesped")
public class GuestController {
    @Autowired
    private GuestService guestService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/guest/login")
    @Operation(summary = "Iniciar sesión como huesped",
            description = "Valida las credenciales del huesped y devuelve sus datos si son correctos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GuestDto.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })

    public ResponseEntity<JwtResponseDTO> loginGuest(
            @Parameter(description = "Credenciales del huesped (email, password).", required = true)
            @RequestBody UserLoginDTO dto) {
        try {
            GuestDto guest = guestService.login(dto);
            // Generar token JWT con email, id y rol
            String token = jwtUtil.generateToken(
                    guest.email(),
                    guest.id(),
                    guest.role().name()
            );
            // Crear objeto de respuesta
            JwtResponseDTO response = new JwtResponseDTO(
                    token,
                    "Bearer",
                    guest.role().name(),
                    guest.id(),
                    guest.email()
            );
            return ResponseEntity.ok(response);

        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Registra un huesped en el sistema
     *
     * @param dto Objeto con los datos del huesped a crear
     * @return ResponseEntity con el DTO del huesped creado
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrar un huesped",
            description = "Crea un nuevo huesped en la base de datos")
    public ResponseEntity<GuestDto> registerGuest(@ModelAttribute CreateGuestDto dto) {
        try {
            return ResponseEntity.ok(guestService.registerGuest(dto));
        } catch (RepeatedElementException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (InvalidElementException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Editar la info del huesped existente
     *
     * @param dto Objeto con los datos a actualizar.
     * @return ResponseEntity con el DTO del huesped actualizado
     */
    @PutMapping("/edit/{id}")
    @Operation(summary = "Editar informacion especifica del huesped",
            description = "Edita los detalles de un huesped, solo los campos permitidos")
    public ResponseEntity<GuestDto> editGuest(@PathVariable Long id, @Valid @ModelAttribute EditGuestDto dto) {
        try {
            return ResponseEntity.ok(guestService.editGuest(id, dto));
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


     /**
      *  Eliminar un huesped del sistema
      * @param id Identificador único del huesped
      * @param dto objeto con los datos a eliminar
      * @return ResponseEntity vacio si la operación es exitosa
      */
     @DeleteMapping("/delete/{id}")
     @Operation(summary = "Eliminar un huesped",
             description = "Permite eliminar a un huesped de la base de datos")
     public ResponseEntity<Void> deleteGuest(@PathVariable Long id, @Valid @RequestBody DeleteGuestDto dto) {
        try {
            guestService.deleteGuest(id, dto);
            return ResponseEntity.noContent().build();
        } catch (ElementNotFoundException | InvalidElementException e) {
            return ResponseEntity.badRequest().body(null);
        }
     }
     /**
      * Obtener los detalles de un huesped
      * @param id Identificador único del huesped.
      * @return ResponseEntity con el DTO del huesped encontrado
      */
     @GetMapping("/{id}")
     @Operation(summary = "Obtener huesped por ID",
             description = "Permite obtener la información especifica de un huesped")
     public ResponseEntity<GuestDto> getGuestById(@PathVariable Long id) {
         try {
             return ResponseEntity.ok(guestService.getGuestById(id));
         } catch (Exception e) {
             return ResponseEntity.notFound().build();
         }
     }
    // ---------------------------
    // GENERAR CÓDIGO DE CAMBIO DE CLAVE
    // ---------------------------
    /**
     * @param dto del huesped
     * @return ResponseEntity con el DTO del codigo generado
     */
     @PostMapping("/password/generate-reset-code")
     @Operation(summary = "Permite generar codigo de reseteo de la contraseña")
     public ResponseEntity<?> generateResetCode(@Valid @RequestBody GenerateResetCodeDto dto) {
         try {
             return ResponseEntity.ok(guestService.generateResetCode(dto.email()));
         } catch (ElementNotFoundException e) {
             return ResponseEntity.badRequest().body(null);
         }
     }
    // ---------------------------
    //  CAMBIAR CONTRASEÑA CON CODIGO VERIFICADO
    // ---------------------------
     @PostMapping("/password/reset")
     @Operation(summary = "Restablecer contraseña con codigo de verificación")
     public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
         try {
             guestService.resetPassword(dto);
             return ResponseEntity.ok("Contraseña restablecida correctamente");
         } catch (ElementNotFoundException | InvalidElementException e) {
             return ResponseEntity.badRequest().body(null);
         }
     }
    // ---------------------------
    // CAMBIAR CONTRASEÑA (AUTENTICADO)
    // ---------------------------
    @PutMapping("/{id}/password/change")
    @Operation (summary = "Permite al huesped cambiar la contraseña de forma voluntaria")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDto dto) {
         try {
             guestService.changePassword(id,dto);
             return ResponseEntity.ok("Contraseña cambiada exitosamente");
         } catch (InvalidElementException | ElementNotFoundException e) {
             return ResponseEntity.badRequest().body(null);
         }
    }
     /**
      *  valor de prueba
      * @return resultado
      */
     @GetMapping("/test")
     public ResponseEntity<String> test() {
         return ResponseEntity.ok("¡Funciona la prueba de salida de endpoint!");
     }
}
