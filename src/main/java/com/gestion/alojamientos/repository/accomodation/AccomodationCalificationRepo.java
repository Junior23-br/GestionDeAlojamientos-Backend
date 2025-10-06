package com.gestion.alojamientos.repository.accomodation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.accomodation.AccomodationCalification;

@Repository
public interface AccomodationCalificationRepo extends JpaRepository<AccomodationCalification, Long> {
    
    /**
     * Encuentra calificaciones por Accommodation ID
     */
    List<AccomodationCalification> findByAccomodationId(Long accommodationId);
    
    /**
     * Calcula promedio de calificación por Accommodation
     */
    @Query("SELECT AVG(ac.prom) FROM AccomodationCalification ac " +
           "WHERE ac.accomodation.id = :accommodationId")
    Optional<Double> calculateAverageRating(@Param("accommodationId") Long accommodationId);
    
    /**
     * Cuenta calificaciones por Accommodation
     */
    Long countByAccomodationId(Long accommodationId);
    
    /**
     * Encuentra calificaciones recientes
     */
    @Query("SELECT ac FROM AccomodationCalification ac " +
           "WHERE ac.accomodation.id = :accommodationId " +
           "ORDER BY ac.id DESC")
    List<AccomodationCalification> findRecentByAccommodationId(@Param("accommodationId") Long accommodationId, Pageable pageable);
}