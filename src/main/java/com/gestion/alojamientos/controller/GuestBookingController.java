package com.gestion.alojamientos.controller;

// IMPORTACIONES PARA SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

// IMPORTACIONES DE SPRING
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.gestion.alojamientos.dto.booking.BookingCreateDTO;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.BookingUpdateDTO;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/guest/bookings")
@Tag(name = "Guest Bookings", description = "Endpoints para la gestión de reservas desde la perspectiva del huésped (Guest). Requiere autenticación JWT con rol ROLE_GUEST. Cubre creación, consulta de historial, modificación y cancelación de reservas, integrando validaciones de disponibilidad, políticas de cancelación y notificaciones por email.")
public class GuestBookingController {

    // ENDPOINT: GET /api/v1/guest/bookings
    @GetMapping
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Listar historial de reservas del huésped",
        description = "Recupera el historial de reservas del huésped autenticado, con filtros opcionales por estado (e.g., PENDING, CONFIRMED, CANCELLED) y paginación para escalabilidad. Cumple con el requerimiento de historial accesible por usuarios, usando índices en DB para rendimiento óptimo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista paginada de reservas recuperada exitosamente.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la consulta.")
    })
    public ResponseEntity<List<BookingDTO>> getGuestBookings(
        @Parameter(description = "Filtro por estado de la reserva (e.g., PENDING, CONFIRMED, CANCELLED). Opcional.", required = false, example = "CONFIRMED") 
        @RequestParam(required = false) String status,
        @Parameter(description = "Número de página para paginación (0-based).", required = false, example = "0") 
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamaño de la página (máximo 10 por requerimientos de usabilidad).", required = false, example = "10") 
        @RequestParam(defaultValue = "10") int size
    ) {
        // Lógica placeholder (en implementación real: llamar a service para consulta paginada con filtros)
        return ResponseEntity.ok(List.of(new BookingDTO()));
    }

    // ENDPOINT: POST /api/v1/guest/bookings
    @PostMapping
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear una nueva reserva",
        description = "Crea una reserva para el huésped autenticado, validando disponibilidad de fechas, capacidad máxima del alojamiento y otros constraints. Genera una transacción pendiente y envía notificación por email al anfitrión. El estado inicial es PENDING. Cumple con el requerimiento de creación de reservas y notificaciones asociadas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente, con ID generado y detalles retornados.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
        @ApiResponse(responseCode = "400", description = "Validación fallida (e.g., fechas no disponibles, capacidad excedida, datos inválidos en DTO)."),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
        @ApiResponse(responseCode = "403", description = "Rol no autorizado (debe ser ROLE_GUEST)."),
        @ApiResponse(responseCode = "500", description = "Error interno durante la creación o notificación.")
    })
    public ResponseEntity<BookingDTO> createBooking(
        @Parameter(description = "DTO con datos requeridos para la reserva (e.g., accommodationId, startDate, endDate, guestsNumber). Validado en capa de servicio.", required = true) 
        @RequestBody BookingCreateDTO dto
    ) {
        // Lógica placeholder (en implementación real: validar disponibilidad, crear entidad Booking, asociar Transaction, notificar via email service)
        return ResponseEntity.status(201).body(new BookingDTO());
    }

    // ENDPOINT: PUT /api/v1/guest/bookings/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Modificar una reserva existente",
        description = "Actualiza detalles de una reserva (e.g., fechas, número de huéspedes) solo si el huésped autenticado es el propietario y las políticas de modificación lo permiten (e.g., no cancelada, dentro de plazo definido). Revalida disponibilidad y envía notificación al anfitrión. Cumple con el requerimiento de modificación de reservas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva modificada exitosamente, con detalles actualizados retornados.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
        @ApiResponse(responseCode = "400", description = "Validación fallida (e.g., nuevas fechas no disponibles, fuera de política)."),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
        @ApiResponse(responseCode = "403", description = "No autorizado: huésped no es propietario de la reserva."),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada por ID."),
        @ApiResponse(responseCode = "500", description = "Error interno durante la actualización o notificación.")
    })
    public ResponseEntity<BookingDTO> updateBooking(
        @Parameter(description = "ID UUID de la reserva a modificar. Debe existir y pertenecer al huésped autenticado.", required = true, example = "123e4567-e89b-12d3-a456-426614174000") 
        @PathVariable UUID id,
        @Parameter(description = "DTO con datos parciales a actualizar (e.g., newStartDate, newEndDate). Validado en service.", required = true) 
        @RequestBody BookingUpdateDTO dto
    ) {
        // Lógica placeholder (en implementación real: verificar propiedad, políticas, revalidar, actualizar entidad, notificar)
        return ResponseEntity.ok(new BookingDTO());
    }

    // ENDPOINT: DELETE /api/v1/guest/bookings/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Cancelar una reserva existente",
        description = "Cancela una reserva (actualiza estado a CANCELLED) solo si el huésped autenticado es el propietario y se cumple la política de cancelación (e.g., dentro de plazo para reembolso). Maneja reembolsos vía transacción asociada y envía notificación al anfitrión. No elimina físicamente (soft cancel). Cumple con el requerimiento de cancelación y notificaciones."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reserva cancelada exitosamente (no content returned)."),
        @ApiResponse(responseCode = "400", description = "Fuera de plazo de cancelación o validación fallida."),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
        @ApiResponse(responseCode = "403", description = "No autorizado: huésped no es propietario de la reserva."),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada por ID."),
        @ApiResponse(responseCode = "500", description = "Error interno durante la cancelación, reembolso o notificación.")
    })
    public ResponseEntity<Void> cancelBooking(
        @Parameter(description = "ID UUID de la reserva a cancelar. Debe existir y pertenecer al huésped autenticado.", required = true, example = "123e4567-e89b-12d3-a456-426614174000") 
        @PathVariable UUID id
    ) {
        // Lógica placeholder (en implementación real: verificar propiedad y políticas, actualizar estado, manejar reembolso, notificar)
        return ResponseEntity.noContent().build();
    }
}