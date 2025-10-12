package com.gestion.alojamientos.service.Impl;
import com.gestion.alojamientos.service.MapboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MapboxServiceImpl implements MapboxService {

    private static final Logger log = LoggerFactory.getLogger(MapboxServiceImpl.class);

    private final RestTemplate mapboxRestTemplate;

    @Value("${mapbox.api.key}")
    private String mapboxApiKey;

    private static final String MAPBOX_BASE_URL = "https://api.mapbox.com";

    public MapboxServiceImpl(RestTemplate mapboxRestTemplate) {
        this.mapboxRestTemplate = mapboxRestTemplate;
    }

    @Override
    public boolean validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            log.warn("Coordenadas nulas proporcionadas");
            return false;
        }

        boolean isValid = latitude >= -90 && latitude <= 90 &&
                longitude >= -180 && longitude <= 180;

        if (!isValid) {
            log.warn("Coordenadas inválidas: lat={}, lng={}", latitude, longitude);
        }

        return isValid;
    }

    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLatRad = Math.toRadians(lat2 - lat1);
        double deltaLonRad = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    @Override
    public String getApiKey() {
        return mapboxApiKey;
    }

    @Override
    public boolean isServiceAvailable() {
        try {
            // Verificación simple de disponibilidad del servicio
            return mapboxApiKey != null && !mapboxApiKey.trim().isEmpty();
        } catch (Exception e) {
            log.error("Error verificando disponibilidad del servicio Mapbox", e);
            return false;
        }
    }
}