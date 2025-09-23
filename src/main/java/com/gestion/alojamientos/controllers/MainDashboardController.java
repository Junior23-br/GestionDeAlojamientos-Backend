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

import com.gestion.alojamientos.dto.GlobalDashboardSummaryDTO;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Main Dashboard", description = "Endpoints para el dashboard principal de administradores (ROLE_ADMIN). Proporciona estadísticas globales con autenticación JWT, optimizado para monitoreo del sistema.")
public class MainDashboardController {

    // ENDPOINT: GET /api/v1/dashboard
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener resumen global del dashboard principal",
        description = "Recupera estadísticas agregadas del sistema para administradores autenticados, incluyendo:\n" +
            "- **Usuarios**: Conteo total y por roles (Guest, Host, Admin).\n" +
            "- **Reservas**: Totales, activas, pendientes, canceladas y métricas por estado.\n" +
            "- **Ingresos totales**: Acumulados de transacciones (Transaction).\n" +
            "- **Otras métricas**: Alojamientos activos, calificaciones promedio, notificaciones enviadas.\n" +
            "Utiliza consultas agregadas en la base de datos para rendimiento óptimo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen global recuperado exitosamente.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalDashboardSummaryDTO.class))),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
        @ApiResponse(responseCode = "403", description = "Rol no autorizado (debe ser ROLE_ADMIN)."),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la agregación de datos.")
    })
    public ResponseEntity<GlobalDashboardSummaryDTO> getMainDashboard() {
        // Lógica placeholder (en implementación real: consultar agregados de User, Booking, Transaction, etc.)
        return ResponseEntity.ok(new GlobalDashboardSummaryDTO());
    }
}