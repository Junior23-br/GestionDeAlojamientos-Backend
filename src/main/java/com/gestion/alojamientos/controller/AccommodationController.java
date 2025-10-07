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
//import com.gestion.alojamientos.dto.accommodation.AccommodationCreateDTO;
//import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
//import com.gestion.alojamientos.dto.accommodation.AccommodationUpdateDTO;
//
//import org.springframework.security.access.prepost.PreAuthorize;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/accommodations")
//@Tag(name = "Accommodations", description = "Endpoints para la gestión de alojamientos. Incluye búsqueda pública y operaciones de creación, actualización y eliminación lógica para anfitriones (ROLE_HOST) con autenticación JWT. Integra Mapbox para ubicaciones y soporta hasta 10 imágenes por alojamiento.")
//public class AccommodationController {
//
//    // ENDPOINT: GET /api/v1/accommodations
//    @GetMapping
//    @Operation(
//        summary = "Buscar alojamientos disponibles",
//        description = "Consulta alojamientos disponibles filtrando por ciudad, fechas de disponibilidad, precio máximo y paginación. Utiliza Mapbox para geolocalización y valida disponibilidad contra reservas existentes. Filtros opcionales para flexibilidad de búsqueda."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Lista paginada de alojamientos disponibles recuperada exitosamente.",
//            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationDTO.class))),
//        @ApiResponse(responseCode = "400", description = "Parámetros inválidos (e.g., fechas inconsistentes, precio negativo)."),
//        @ApiResponse(responseCode = "500", description = "Error interno del servidor durante la consulta o integración con Mapbox.")
//    })
//    public ResponseEntity<List<AccommodationDTO>> searchAccommodations(
//        @Parameter(description = "Ciudad para filtrar alojamientos (e.g., Madrid). Opcional.", required = false, example = "Madrid")
//        @RequestParam(required = false) String city,
//        @Parameter(description = "Fecha de inicio de disponibilidad (formato YYYY-MM-DD). Opcional.", required = false, example = "2025-10-01")
//        @RequestParam(required = false) String startDate,
//        @Parameter(description = "Fecha de fin de disponibilidad (formato YYYY-MM-DD). Opcional.", required = false, example = "2025-10-10")
//        @RequestParam(required = false) String endDate,
//        @Parameter(description = "Precio máximo por noche (en USD). Opcional.", required = false, example = "100.0")
//        @RequestParam(required = false) Double maxPrice,
//        @Parameter(description = "Número de página (0-based) para paginación.", required = false, example = "0")
//        @RequestParam(defaultValue = "0") int page,
//        @Parameter(description = "Tamaño de la página (máximo 10 por requerimientos de usabilidad).", required = false, example = "10")
//        @RequestParam(defaultValue = "10") int size
//    ) {
//        // Lógica placeholder (en implementación real: consultar DB con filtros, integrar Mapbox, paginar)
//        return ResponseEntity.ok(List.of(new AccommodationDTO()));
//    }
//
//    // ENDPOINT: POST /api/v1/accommodations
//    @PostMapping
//    @PreAuthorize("hasRole('ROLE_HOST')")
//    @SecurityRequirement(name = "bearerAuth")
//    @Operation(
//        summary = "Crear un nuevo alojamiento",
//        description = "Crea un alojamiento por un anfitrión autenticado (ROLE_HOST), permitiendo hasta 10 imágenes (URLs externas) y datos como dirección, precio y capacidad. Valida propiedad y consistencia de datos antes de persistir. Cumple con el requerimiento de creación de alojamientos."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "201", description = "Alojamiento creado exitosamente, con ID generado y detalles retornados.",
//            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationDTO.class))),
//        @ApiResponse(responseCode = "400", description = "Validación fallida (e.g., más de 10 imágenes, datos incompletos)."),
//        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
//        @ApiResponse(responseCode = "403", description = "Rol no autorizado (debe ser ROLE_HOST)."),
//        @ApiResponse(responseCode = "500", description = "Error interno durante la creación.")
//    })
//    public ResponseEntity<AccommodationDTO> createAccommodation(
//        @Parameter(description = "DTO con datos requeridos para el alojamiento (e.g., address, pricePerNight, images[0-9]). Validado en capa de servicio.", required = true)
//        @RequestBody AccommodationCreateDTO dto
//    ) {
//        // Lógica placeholder (en implementación real: validar imágenes, asociar a host, persistir)
//        return ResponseEntity.status(201).body(new AccommodationDTO());
//    }
//
//    // ENDPOINT: GET /api/v1/accommodations/{id}
//    @GetMapping("/{id}")
//    @Operation(
//        summary = "Obtener detalles de un alojamiento",
//        description = "Recupera detalles de un alojamiento específico, incluyendo ubicación (vía Mapbox), precio, capacidad y promedio de calificaciones de comentarios. Accesible públicamente para huéspedes y anfitriones."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Detalles del alojamiento recuperados exitosamente.",
//            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationDTO.class))),
//        @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado por ID."),
//        @ApiResponse(responseCode = "500", description = "Error interno durante la consulta o integración con Mapbox.")
//    })
//    public ResponseEntity<AccommodationDTO> getAccommodationById(
//        @Parameter(description = "ID UUID del alojamiento a consultar. Debe existir.", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
//        @PathVariable UUID id
//    ) {
//        // Lógica placeholder (en implementación real: consultar DB, calcular promedio de reviews, integrar Mapbox)
//        return ResponseEntity.ok(new AccommodationDTO());
//    }
//
//    // ENDPOINT: PUT /api/v1/accommodations/{id}
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_HOST')")
//    @SecurityRequirement(name = "bearerAuth")
//    @Operation(
//        summary = "Actualizar un alojamiento existente",
//        description = "Actualiza parcialmente los datos de un alojamiento (e.g., precio, descripción) solo si el anfitrión autenticado es el propietario. Valida cambios y mantiene consistencia con reservas asociadas. Cumple con el requerimiento de soft update."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Alojamiento actualizado exitosamente, con detalles retornados.",
//            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationDTO.class))),
//        @ApiResponse(responseCode = "400", description = "Validación fallida (e.g., datos inválidos, conflicto con reservas)."),
//        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
//        @ApiResponse(responseCode = "403", description = "No autorizado: anfitrión no es propietario."),
//        @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado por ID."),
//        @ApiResponse(responseCode = "500", description = "Error interno durante la actualización.")
//    })
//    public ResponseEntity<AccommodationDTO> updateAccommodation(
//        @Parameter(description = "ID UUID del alojamiento a actualizar. Debe existir y pertenecer al anfitrión.", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
//        @PathVariable UUID id,
//        @Parameter(description = "DTO con datos parciales a actualizar (e.g., pricePerNight, description). Validado en service.", required = true)
//        @RequestBody AccommodationUpdateDTO dto
//    ) {
//        // Lógica placeholder (en implementación real: verificar propiedad, validar, actualizar)
//        return ResponseEntity.ok(new AccommodationDTO());
//    }
//
//    // ENDPOINT: DELETE /api/v1/accommodations/{id}
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_HOST')")
//    @SecurityRequirement(name = "bearerAuth")
//    @Operation(
//        summary = "Eliminar lógicamente un alojamiento",
//        description = "Marca un alojamiento como 'deleted' (soft delete) solo si el anfitrión autenticado es el propietario. No elimina físicamente para mantener historial de reservas asociadas. Cumple con el requerimiento de eliminación lógica."
//    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "204", description = "Alojamiento eliminado lógicamente exitosamente (no content returned)."),
//        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token JWT inválido."),
//        @ApiResponse(responseCode = "403", description = "No autorizado: anfitrión no es propietario."),
//        @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado por ID."),
//        @ApiResponse(responseCode = "500", description = "Error interno durante la eliminación.")
//    })
//    public ResponseEntity<Void> deleteAccommodation(
//        @Parameter(description = "ID UUID del alojamiento a eliminar. Debe existir y pertenecer al anfitrión.", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
//        @PathVariable UUID id
//    ) {
//        // Lógica placeholder (en implementación real: verificar propiedad, actualizar estado a deleted)
//        return ResponseEntity.noContent().build();
//    }
//}