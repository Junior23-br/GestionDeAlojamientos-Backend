package com.gestion.alojamientos.service;

import org.springframework.stereotype.Service;

/**
 * Servicio para integración con Mapbox.
 * Proporciona funcionalidades básicas para trabajar con ubicaciones geográficas.
 */
public interface MapboxService {

    /**
     * Valida si las coordenadas proporcionadas son válidas.
     *
     * @param latitude Latitud (-90 a 90)
     * @param longitude Longitud (-180 a 180)
     * @return true si las coordenadas son válidas, false en caso contrario
     */
    boolean validateCoordinates(Double latitude, Double longitude);

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine.
     *
     * @param lat1 Latitud del primer punto
     * @param lon1 Longitud del primer punto
     * @param lat2 Latitud del segundo punto
     * @param lon2 Longitud del segundo punto
     * @return Distancia en kilómetros
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);

    /**
     * Obtiene el token de API de Mapbox para uso interno.
     *
     * @return Token de API de Mapbox
     */
    String getApiKey();

    /**
     * Verifica si el servicio de Mapbox está disponible.
     *
     * @return true si el servicio está disponible, false en caso contrario
     */
    boolean isServiceAvailable();
}