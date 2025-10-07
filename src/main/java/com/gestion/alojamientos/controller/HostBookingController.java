//package com.gestion.alojamientos.controller;
//
//// IMPORTACIONES PARA SWAGGER
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//
//// IMPORTACIONES DE SPRING
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.gestion.alojamientos.dto.booking.BookingDTO;
//
//import org.springframework.security.access.prepost.PreAuthorize;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/host/bookings")
//@Tag(name = "Host Bookings", description = "Endpoints para la gestión de reservas desde la perspectiva del anfitrión (ROLE_HOST). Requiere autenticación JWT y verifica propiedad sobre los alojamientos asociados. Incluye listado, aprobación y cancelación de reservas con notificaciones al huésped.")
//public class HostBookingController {
//
//    // ENDPOINT: GET /api/v1/host/bookings
//    @GetMapping
//    @PreAuthorize("hasRole('ROLE_HOST')")
//    @SecurityRequirement(name = "bearerAuth")
//    @Operation(
//        summary = "Listar reservas de los alojamientos del anfitrión",
//        description = "Recupera el historial de reservas asociadas a los alojamientos del anfitrión autenticado, con filtros opcionales por estado (e.g., PENDING, CONFIRMED, CANCELLED) y paginación para escalabilidad. Utiliza índices en la base de datos para optimizar rendimiento."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Lista paginada de reservas recuperada exitosamente.",
//            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
//        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
//        @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la consulta.")
//    })
//    public ResponseEntity<List<BookingDTO>> getHostBookings(
//        @Parameter(description = "Filtro por estado de la reserva (e.g., PENDING, CONFIRMED, CANCELLED). Opcional.", required = false, example = "PENDING")
//        @RequestParam(required = false) String status,
//        @Parameter(description = "Número de página (0-based) para paginación.", required = false, example = "0")
//        @RequestParam(defaultValue = "0") int page,
//        @Parameter(description = "Tamaño de la página (máximo 10 por requerimientos de usabilidad).", required = false, example = "10")
//        @RequestParam(defaultValue = "10") int size
//    ) {
//        // Lógica placeholder (en implementación real: consultar Booking por alojamientos del host, aplicar filtros, paginar)
//        return ResponseEntity.ok(List.of(new BookingDTO()));
//    }
//
//    // ENDPOINT: PUT /api/v1/host/bookings/{id}/approve
//    @PutMapping("/{id}/approve")
//    @PreAuthorize("hasRole('ROLE_HOST')")
//    @SecurityRequirement(name = "bearerAuth")
//    @Operation(
//        summary = "Aprobar una reserva",
//        description = "Aprueba una reserva en estado PENDING si el anfitrión autenticado es propietario del alojamiento asociado, cambiando el estado a CONFIRMED y generando una notificación por email al huésped. Valida disponibilidad y políticas de aprobación."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Reserva aprobada exitosamente, con detalles actualizados retornados.",
//            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
//        @ApiResponse(responseCode = "400", description = "Estado inválido (e.g., reserva ya confirmada o cancelada)."),
//        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
//        @ApiResponse(responseCode = "403", description = "No autorizado: anfitrión no es propietario del alojamiento."),
//        @ApiResponse(responseCode = "404", description = "Reserva no encontrada por ID."),
//        @ApiResponse(responseCode = "500", description = "Error interno durante la aprobación o notificación.")
//    })
//    public ResponseEntity<BookingDTO> approveBooking(
//        @Parameter(description = "ID UUID de la reserva a aprobar. Debe existir y pertenecer a un alojamiento del anfitrión.", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
//        @PathVariable UUID id
//    ) {
//        // Lógica placeholder (en implementación real: verificar propiedad, validar estado, actualizar, notificar)
//        return ResponseEntity.ok(new BookingDTO());
//    }
//
//    // ENDPOINT: PUT /api/v1/host/bookings/{id}/cancel
//    @PutMapping("/{id}/cancel")
//    @PreAuthorize("hasRole('ROLE_HOST')")
//    @SecurityRequirement(name = "bearerAuth")
//    @Operation(
//        summary = "Cancelar una reserva por el anfitrión",
//        description = "Cancela una reserva si el anfitrión autenticado es propietario del alojamiento asociado, respetando políticas de cancelación (e.g., plazos para reembolso) y generando una notificación por email al huésped. Actualiza el estado a CANCELLED."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente, con detalles actualizados retornados.",
//            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
//        @ApiResponse(responseCode = "400", description = "Fuera de plazo de cancelación o validación fallida."),
//        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
//        @ApiResponse(responseCode = "403", description = "No autorizado: anfitrión no es propietario del alojamiento."),
//        @ApiResponse(responseCode = "404", description = "Reserva no encontrada por ID."),
//        @ApiResponse(responseCode = "500", description = "Error interno durante la cancelación, reembolso o notificación.")
//    })
//    public ResponseEntity<BookingDTO> cancelBookingByHost(
//        @Parameter(description = "ID UUID de la reserva a cancelar. Debe existir y pertenecer a un alojamiento del anfitrión.", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
//        @PathVariable UUID id
//    ) {
//        // Lógica placeholder (en implementación real: verificar propiedad, validar políticas, actualizar estado, notificar, manejar reembolso)
//        return ResponseEntity.ok(new BookingDTO());
//    }
//}