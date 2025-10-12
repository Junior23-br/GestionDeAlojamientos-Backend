package com.gestion.alojamientos.controller;

import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.model.accomodation.Ubication;
import com.gestion.alojamientos.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de ubicaciones y coordenadas de alojamientos.
 * Proporciona endpoints para registrar, actualizar y consultar ubicaciones geográficas.
 */
@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location Management", description = "API para gestión de ubicaciones de alojamientos")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Actualiza las coordenadas de un alojamiento existente.
     * 
     * @param accommodationId ID del alojamiento
     * @param latitude Nueva latitud
     * @param longitude Nueva longitud
     * @return Ubicación actualizada
     */
    @PutMapping("/accommodation/{accommodationId}/coordinates")
    @Operation(summary = "Actualizar coordenadas", 
               description = "Actualiza las coordenadas de un alojamiento existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coordenadas actualizadas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado"),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    public ResponseEntity<Ubication> updateAccommodationCoordinates(
            @Parameter(description = "ID del alojamiento") @PathVariable Long accommodationId,
            @Parameter(description = "Latitud (-90 a 90)") @RequestParam Double latitude,
            @Parameter(description = "Longitud (-180 a 180)") @RequestParam Double longitude) {
        
        try {
            Ubication updatedUbication = locationService.updateAccommodationCoordinates(
                    accommodationId, latitude, longitude);
            return ResponseEntity.ok(updatedUbication);
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Registra nuevas coordenadas para un alojamiento.
     * 
     * @param accommodationId ID del alojamiento
     * @param latitude Latitud
     * @param longitude Longitud
     * @return Ubicación creada o actualizada
     */
    @PostMapping("/accommodation/{accommodationId}/coordinates")
    @Operation(summary = "Registrar coordenadas", 
               description = "Registra nuevas coordenadas para un alojamiento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coordenadas registradas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alojamiento no encontrado"),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    public ResponseEntity<Ubication> registerAccommodationCoordinates(
            @Parameter(description = "ID del alojamiento") @PathVariable Long accommodationId,
            @Parameter(description = "Latitud (-90 a 90)") @RequestParam Double latitude,
            @Parameter(description = "Longitud (-180 a 180)") @RequestParam Double longitude) {
        
        try {
            Ubication ubication = locationService.registerAccommodationCoordinates(
                    accommodationId, latitude, longitude);
            return ResponseEntity.ok(ubication);
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene las coordenadas de un alojamiento.
     * 
     * @param accommodationId ID del alojamiento
     * @return Ubicación con coordenadas
     */
    @GetMapping("/accommodation/{accommodationId}/coordinates")
    @Operation(summary = "Obtener coordenadas", 
               description = "Obtiene las coordenadas de un alojamiento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coordenadas obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alojamiento o coordenadas no encontradas")
    })
    public ResponseEntity<Ubication> getAccommodationCoordinates(
            @Parameter(description = "ID del alojamiento") @PathVariable Long accommodationId) {
        
        try {
            Ubication ubication = locationService.getAccommodationCoordinates(accommodationId);
            return ResponseEntity.ok(ubication);
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca alojamientos cercanos a un punto específico.
     * 
     * @param latitude Latitud del punto de referencia
     * @param longitude Longitud del punto de referencia
     * @param radiusKm Radio de búsqueda en kilómetros
     * @return Lista de ubicaciones cercanas
     */
    @GetMapping("/nearby")
    @Operation(summary = "Buscar alojamientos cercanos", 
               description = "Busca alojamientos cercanos a un punto específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    public ResponseEntity<List<Ubication>> findNearbyAccommodations(
            @Parameter(description = "Latitud del punto de referencia") @RequestParam Double latitude,
            @Parameter(description = "Longitud del punto de referencia") @RequestParam Double longitude,
            @Parameter(description = "Radio de búsqueda en kilómetros") @RequestParam Double radiusKm) {
        
        try {
            List<Ubication> nearbyUbications = locationService.findNearbyAccommodations(
                    latitude, longitude, radiusKm);
            return ResponseEntity.ok(nearbyUbications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Verifica si un alojamiento tiene coordenadas registradas.
     * 
     * @param accommodationId ID del alojamiento
     * @return true si tiene coordenadas, false en caso contrario
     */
    @GetMapping("/accommodation/{accommodationId}/has-coordinates")
    @Operation(summary = "Verificar coordenadas", 
               description = "Verifica si un alojamiento tiene coordenadas registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación realizada exitosamente")
    })
    public ResponseEntity<Boolean> hasCoordinates(
            @Parameter(description = "ID del alojamiento") @PathVariable Long accommodationId) {
        
        boolean hasCoordinates = locationService.hasCoordinates(accommodationId);
        return ResponseEntity.ok(hasCoordinates);
    }

    /**
     * Calcula la distancia entre dos alojamientos.
     * 
     * @param accommodationId1 ID del primer alojamiento
     * @param accommodationId2 ID del segundo alojamiento
     * @return Distancia en kilómetros
     */
    @GetMapping("/distance")
    @Operation(summary = "Calcular distancia", 
               description = "Calcula la distancia entre dos alojamientos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Distancia calculada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Uno o ambos alojamientos no encontrados")
    })
    public ResponseEntity<Double> calculateDistanceBetweenAccommodations(
            @Parameter(description = "ID del primer alojamiento") @RequestParam Long accommodationId1,
            @Parameter(description = "ID del segundo alojamiento") @RequestParam Long accommodationId2) {
        
        try {
            double distance = locationService.calculateDistanceBetweenAccommodations(
                    accommodationId1, accommodationId2);
            return ResponseEntity.ok(distance);
        } catch (ElementNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
