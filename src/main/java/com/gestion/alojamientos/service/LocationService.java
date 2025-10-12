package com.gestion.alojamientos.service;

import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.model.accomodation.Ubication;

import java.util.List;

/**
 * Servicio para gestión de ubicaciones y coordenadas de alojamientos.
 * Integra funcionalidades de Mapbox para manejo de ubicaciones geográficas.
 */
public interface LocationService {

    /**
     * Actualiza las coordenadas de un alojamiento existente.
     *
     * @param accommodationId ID del alojamiento
     * @param latitude Nueva latitud
     * @param longitude Nueva longitud
     * @return Ubicación actualizada
     * @throws ElementNotFoundException si el alojamiento no existe
     */
    Ubication updateAccommodationCoordinates(Long accommodationId, Double latitude, Double longitude)
            throws ElementNotFoundException;

    /**
     * Registra nuevas coordenadas para un alojamiento.
     *
     * @param accommodationId ID del alojamiento
     * @param latitude Latitud
     * @param longitude Longitud
     * @return Ubicación creada o actualizada
     * @throws ElementNotFoundException si el alojamiento no existe
     */
    Ubication registerAccommodationCoordinates(Long accommodationId, Double latitude, Double longitude)
            throws ElementNotFoundException;

    /**
     * Obtiene las coordenadas de un alojamiento.
     *
     * @param accommodationId ID del alojamiento
     * @return Ubicación con coordenadas
     * @throws ElementNotFoundException si el alojamiento no existe o no tiene ubicación
     */
    Ubication getAccommodationCoordinates(Long accommodationId) throws ElementNotFoundException;

    /**
     * Busca alojamientos cercanos a un punto específico.
     *
     * @param latitude Latitud del punto de referencia
     * @param longitude Longitud del punto de referencia
     * @param radiusKm Radio de búsqueda en kilómetros
     * @return Lista de ubicaciones cercanas
     */
    List<Ubication> findNearbyAccommodations(Double latitude, Double longitude, Double radiusKm);

    /**
     * Verifica si un alojamiento tiene coordenadas registradas.
     *
     * @param accommodationId ID del alojamiento
     * @return true si tiene coordenadas, false en caso contrario
     */
    boolean hasCoordinates(Long accommodationId);

    /**
     * Calcula la distancia entre dos alojamientos.
     *
     * @param accommodationId1 ID del primer alojamiento
     * @param accommodationId2 ID del segundo alojamiento
     * @return Distancia en kilómetros
     * @throws ElementNotFoundException si alguno de los alojamientos no existe o no tiene coordenadas
     */
    double calculateDistanceBetweenAccommodations(Long accommodationId1, Long accommodationId2)
            throws ElementNotFoundException;
}