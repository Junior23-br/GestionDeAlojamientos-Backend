package com.gestion.alojamientos.repository;

import com.gestion.alojamientos.model.accomodation.Accomodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface AccomodationRepo extends JpaRepository<Accomodation, Long>, JpaSpecificationExecutor<Accomodation> {
    Optional<Accomodation> findById(Long id);
    Optional<List<Accomodation>> getByHostId(Long id);
}
