package com.gestion.alojamientos.service;

import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.Ubication;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.accomodation.UbicationRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de ubicaciones y coordenadas de alojamientos.
 * Integra funcionalidades de Mapbox para manejo de ubicaciones geográficas.
 */
@Service
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    private final UbicationRepo ubicationRepo;
    private final AccommodationRepo accommodationRepo;
    private final MapboxService mapboxService;

    public LocationService(UbicationRepo ubicationRepo, AccommodationRepo accommodationRepo, MapboxService mapboxService) {
        this.ubicationRepo = ubicationRepo;
        this.accommodationRepo = accommodationRepo;
        this.mapboxService = mapboxService;
    }

    /**
     * Actualiza las coordenadas de un alojamiento existente.
     * 
     * @param accommodationId ID del alojamiento
     * @param latitude Nueva latitud
     * @param longitude Nueva longitud
     * @return Ubicación actualizada
     * @throws ElementNotFoundException si el alojamiento no existe
     */
    @Transactional
    public Ubication updateAccommodationCoordinates(Long accommodationId, Double latitude, Double longitude) 
            throws ElementNotFoundException {
        
        log.info("Actualizando coordenadas para alojamiento ID: {}", accommodationId);
        
        // Validar coordenadas
        if (!mapboxService.validateCoordinates(latitude, longitude)) {
            throw new IllegalArgumentException("Coordenadas inválidas proporcionadas");
        }
        
        // Buscar alojamiento
        Optional<Accomodation> accommodationOpt = accommodationRepo.findByIdWithUbication(accommodationId);
        if (accommodationOpt.isEmpty()) {
            throw new ElementNotFoundException("Alojamiento no encontrado con ID: " + accommodationId);
        }
        
        Accomodation accommodation = accommodationOpt.get();
        Ubication ubication = accommodation.getUbication();
        
        if (ubication == null) {
            throw new ElementNotFoundException("Ubicación no encontrada para el alojamiento ID: " + accommodationId);
        }
        
        // Actualizar coordenadas
        ubication.setLatitud(latitude);
        ubication.setLongitud(longitude);
        
        Ubication savedUbication = ubicationRepo.save(ubication);
        
        log.info("Coordenadas actualizadas exitosamente para alojamiento ID: {} - Lat: {}, Lng: {}", 
                accommodationId, latitude, longitude);
        
        return savedUbication;
    }

    /**
     * Registra nuevas coordenadas para un alojamiento.
     * 
     * @param accommodationId ID del alojamiento
     * @param latitude Latitud
     * @param longitude Longitud
     * @return Ubicación creada o actualizada
     * @throws ElementNotFoundException si el alojamiento no existe
     */
    @Transactional
    public Ubication registerAccommodationCoordinates(Long accommodationId, Double latitude, Double longitude) 
            throws ElementNotFoundException {
        
        log.info("Registrando coordenadas para alojamiento ID: {}", accommodationId);
        
        // Validar coordenadas
        if (!mapboxService.validateCoordinates(latitude, longitude)) {
            throw new IllegalArgumentException("Coordenadas inválidas proporcionadas");
        }
        
        // Buscar alojamiento
        Optional<Accomodation> accommodationOpt = accommodationRepo.findByIdWithUbication(accommodationId);
        if (accommodationOpt.isEmpty()) {
            throw new ElementNotFoundException("Alojamiento no encontrado con ID: " + accommodationId);
        }
        
        Accomodation accommodation = accommodationOpt.get();
        Ubication ubication = accommodation.getUbication();
        
        if (ubication == null) {
            // Crear nueva ubicación si no existe
            ubication = Ubication.builder()
                    .latitud(latitude)
                    .longitud(longitude)
                    .direccion("Ubicación registrada") // Dirección por defecto
                    .build();
            
            accommodation.setUbication(ubication);
        } else {
            // Actualizar ubicación existente
            ubication.setLatitud(latitude);
            ubication.setLongitud(longitude);
        }
        
        Ubication savedUbication = ubicationRepo.save(ubication);
        
        log.info("Coordenadas registradas exitosamente para alojamiento ID: {} - Lat: {}, Lng: {}", 
                accommodationId, latitude, longitude);
        
        return savedUbication;
    }

    /**
     * Obtiene las coordenadas de un alojamiento.
     * 
     * @param accommodationId ID del alojamiento
     * @return Ubicación con coordenadas
     * @throws ElementNotFoundException si el alojamiento no existe o no tiene ubicación
     */
    public Ubication getAccommodationCoordinates(Long accommodationId) throws ElementNotFoundException {
        log.info("Obteniendo coordenadas para alojamiento ID: {}", accommodationId);
        
        Optional<Accomodation> accommodationOpt = accommodationRepo.findByIdWithUbication(accommodationId);
        if (accommodationOpt.isEmpty()) {
            throw new ElementNotFoundException("Alojamiento no encontrado con ID: " + accommodationId);
        }
        
        Accomodation accommodation = accommodationOpt.get();
        Ubication ubication = accommodation.getUbication();
        
        if (ubication == null || ubication.getLatitud() == null || ubication.getLongitud() == null) {
            throw new ElementNotFoundException("Coordenadas no encontradas para el alojamiento ID: " + accommodationId);
        }
        
        log.info("Coordenadas obtenidas para alojamiento ID: {} - Lat: {}, Lng: {}", 
                accommodationId, ubication.getLatitud(), ubication.getLongitud());
        
        return ubication;
    }

    /**
     * Busca alojamientos cercanos a un punto específico.
     * 
     * @param latitude Latitud del punto de referencia
     * @param longitude Longitud del punto de referencia
     * @param radiusKm Radio de búsqueda en kilómetros
     * @return Lista de ubicaciones cercanas
     */
    public List<Ubication> findNearbyAccommodations(Double latitude, Double longitude, Double radiusKm) {
        log.info("Buscando alojamientos cercanos a Lat: {}, Lng: {} en radio de {} km", 
                latitude, longitude, radiusKm);
        
        if (!mapboxService.validateCoordinates(latitude, longitude)) {
            throw new IllegalArgumentException("Coordenadas inválidas proporcionadas");
        }
        
        if (radiusKm == null || radiusKm <= 0) {
            throw new IllegalArgumentException("Radio de búsqueda debe ser mayor a 0");
        }
        
        // Calcular rango de coordenadas aproximado (simplificado)
        double latRange = radiusKm / 111.0; // Aproximadamente 111 km por grado de latitud
        double lngRange = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));
        
        List<Ubication> nearbyUbications = ubicationRepo.findByCoordinatesRange(
                latitude - latRange,
                latitude + latRange,
                longitude - lngRange,
                longitude + lngRange
        );
        
        log.info("Encontrados {} alojamientos en el área especificada", nearbyUbications.size());
        
        return nearbyUbications;
    }

    /**
     * Verifica si un alojamiento tiene coordenadas registradas.
     * 
     * @param accommodationId ID del alojamiento
     * @return true si tiene coordenadas, false en caso contrario
     */
    public boolean hasCoordinates(Long accommodationId) {
        try {
            getAccommodationCoordinates(accommodationId);
            return true;
        } catch (ElementNotFoundException e) {
            return false;
        }
    }

    /**
     * Calcula la distancia entre dos alojamientos.
     * 
     * @param accommodationId1 ID del primer alojamiento
     * @param accommodationId2 ID del segundo alojamiento
     * @return Distancia en kilómetros
     * @throws ElementNotFoundException si alguno de los alojamientos no existe o no tiene coordenadas
     */
    public double calculateDistanceBetweenAccommodations(Long accommodationId1, Long accommodationId2) 
            throws ElementNotFoundException {
        
        Ubication ubication1 = getAccommodationCoordinates(accommodationId1);
        Ubication ubication2 = getAccommodationCoordinates(accommodationId2);
        
        return mapboxService.calculateDistance(
                ubication1.getLatitud(), ubication1.getLongitud(),
                ubication2.getLatitud(), ubication2.getLongitud()
        );
    }
}
