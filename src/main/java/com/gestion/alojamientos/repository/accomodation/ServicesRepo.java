package com.gestion.alojamientos.repository.accomodation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.accomodation.Services;

@Repository
public interface ServicesRepo extends JpaRepository<Services, Long> {
    
    @Query("SELECT s FROM Services s WHERE s.active = true ORDER BY s.name")
    List<Services> findAllActiveServices();
    
    boolean existsByName(String name);
    
    Optional<Services> findByName(String name);
}