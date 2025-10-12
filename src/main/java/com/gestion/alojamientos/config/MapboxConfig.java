package com.gestion.alojamientos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración para la integración con Mapbox.
 * Proporciona configuración segura del token de API y cliente HTTP.
 */
@Configuration
public class MapboxConfig {

    @Value("${mapbox.api.key}")
    private String mapboxApiKey;

    /**
     * Bean para RestTemplate que se utilizará para hacer llamadas HTTP a la API de Mapbox.
     * 
     * @return RestTemplate configurado
     */
    @Bean
    public RestTemplate mapboxRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Bean para el token de API de Mapbox.
     * Se mantiene privado y solo se expone a través del servicio correspondiente.
     * 
     * @return Token de API de Mapbox
     */
    @Bean
    public String mapboxApiKey() {
        return mapboxApiKey;
    }
}
