package com.gestion.alojamientos.repository.accomodation;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.accomodation.Cities;
import com.gestion.alojamientos.model.accomodation.Ubication;



@Repository
public interface UbicationRepo extends JpaRepository<Ubication, Long> {
    
    /**
     * Encuentra ubicaciones por ciudad
     */
    List<Ubication> findByCity(Cities city);
    
    /**
     * Verifica si existe ubicación con misma dirección y ciudad
     */
    boolean existsByDireccionAndCity(String direccion, Cities city);
    
    /**
     * Encuentra ubicaciones cercanas por coordenadas (ejemplo básico)
     */
    @Query("SELECT u FROM Ubication u " +
           "WHERE u.latitud BETWEEN :minLat AND :maxLat " +
           "AND u.longitud BETWEEN :minLng AND :maxLng")
    List<Ubication> findByCoordinatesRange(@Param("minLat") Double minLat, 
                                          @Param("maxLat") Double maxLat,
                                          @Param("minLng") Double minLng, 
                                          @Param("maxLng") Double maxLng);
}