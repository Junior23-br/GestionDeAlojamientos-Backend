package com.gestion.alojamientos.controllers;

// IMPORTACIONES PARA SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

// IMPORTACIONES DE SPRING
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestion.alojamientos.dto.DashboardSummaryDTO;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/guest/dashboard")
@Tag(name = "Guest Dashboard", description = "Endpoints para el dashboard del huésped (ROLE_GUEST). Proporciona un resumen personalizado con autenticación JWT, incluyendo historial de reservas, reservas activas, comentarios pendientes y métricas personales. Optimizado para usabilidad y rendimiento.")
public class GuestDashboardController {

    // ENDPOINT: GET /api/v1/guest/dashboard
    @GetMapping
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener resumen del dashboard del huésped",
        description = "Recupera un resumen consolidado para el huésped autenticado, incluyendo:\n" +
            "- **Historial resumido**: Reservas pasadas (e.g., fechas, estados como CONFIRMED, CANCELLED).\n" +
            "- **Reservas activas**: Reservas actuales (e.g., PENDING, CONFIRMED) con detalles.\n" +
            "- **Comentarios pendientes**: Indicación de reseñas por realizar para alojamientos visitados.\n" +
            "- **Métricas personales**: Estadísticas como total de reservas o gastos acumulados.\n" +
            "Utiliza consultas optimizadas en la base de datos y requiere autenticación JWT."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen del dashboard recuperado exitosamente.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardSummaryDTO.class))),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la consulta de datos o agregación.")
    })
    public ResponseEntity<DashboardSummaryDTO> getGuestDashboard() {
        // Lógica placeholder (en implementación real: consultar Booking, Review, Transaction, aggregrar métricas)
        return ResponseEntity.ok(new DashboardSummaryDTO());
    }
}