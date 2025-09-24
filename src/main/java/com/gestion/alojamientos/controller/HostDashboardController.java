package com.gestion.alojamientos.controller;
// IMPORTACIONES PARA SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

// IMPORTACIONES DE SPRING
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestion.alojamientos.dto.DashboardSummaryDTO;

import org.springframework.security.access.prepost.PreAuthorize;

// IMPORTACIONES PARA SWAGGER
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/v1/host/dashboard")
@Tag(name = "Host Dashboard", description = "Endpoints para el dashboard del anfitrión (ROLE_HOST). Proporciona un resumen personalizado con autenticación JWT, incluyendo ingresos, calificaciones promedio, reservas pendientes y métricas de propiedades. Optimizado para usabilidad y rendimiento.")
public class HostDashboardController {

    // ENDPOINT: GET /api/v1/host/dashboard
    @GetMapping
    @PreAuthorize("hasRole('ROLE_HOST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener resumen del dashboard del anfitrión",
        description = "Recupera un resumen consolidado para el anfitrión autenticado, incluyendo:\n" +
            "- **Ingresos**: Total acumulado o por período, basado en transacciones de reservas.\n" +
            "- **Calificaciones promedio**: Promedio de reseñas recibidas por los alojamientos del anfitrión.\n" +
            "- **Reservas pendientes**: Lista de reservas en estado PENDING que requieren acción.\n" +
            "- **Métricas de propiedades**: Estadísticas como número de alojamientos o ocupación promedio.\n" +
            "Utiliza consultas agregadas optimizadas en la base de datos y requiere autenticación JWT."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen del dashboard recuperado exitosamente.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardSummaryDTO.class))),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la consulta de datos o agregación.")
    })
    public ResponseEntity<DashboardSummaryDTO> getHostDashboard() {
        // Lógica placeholder (en implementación real: consultar Accommodation, Booking, Transaction, Review, aggregrar métricas)
        return ResponseEntity.ok(new DashboardSummaryDTO());
    }
}