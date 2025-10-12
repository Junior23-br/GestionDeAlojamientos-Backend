package com.gestion.alojamientos.service.Impl;

import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.Ubication;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.accomodation.UbicationRepo;
import com.gestion.alojamientos.service.LocationService;
import com.gestion.alojamientos.service.MapboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final UbicationRepo ubicationRepo;
    private final AccommodationRepo accommodationRepo;
    private final MapboxService mapboxService;

    public LocationServiceImpl(UbicationRepo ubicationRepo, AccommodationRepo accommodationRepo, MapboxService mapboxService) {
        this.ubicationRepo = ubicationRepo;
        this.accommodationRepo = accommodationRepo;
        this.mapboxService = mapboxService;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public boolean hasCoordinates(Long accommodationId) {
        try {
            getAccommodationCoordinates(accommodationId);
            return true;
        } catch (ElementNotFoundException e) {
            return false;
        }
    }

    @Override
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