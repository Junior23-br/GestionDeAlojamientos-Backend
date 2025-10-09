package com.gestion.alojamientos.repository.accomodation;

import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationType;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.Cities;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface AccommodationRepo extends JpaRepository<Accomodation, Long>, JpaSpecificationExecutor<Accomodation> {
      /**
     * Encuentra Accommodations por Host ID
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "LEFT JOIN FETCH a.servicesList " +
           "WHERE a.host.id = :hostId")
    List<Accomodation> findByHostIdWithDetails(@Param("hostId") Long hostId);
    
    /**
     * Encuentra Accommodations por Host ID paginadas
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE a.host.id = :hostId")
    Page<Accomodation> findByHostId(@Param("hostId") Long hostId, Pageable pageable);
    
    /**
     * Cuenta Accommodations por estado para un Host
     */
    @Query("SELECT a.approvalStatus, COUNT(a) FROM Accomodation a " +
           "WHERE a.host.id = :hostId " +
           "GROUP BY a.approvalStatus")
    List<Object[]> countAccommodationsByApprovalStatus(@Param("hostId") Long hostId);



    //INFORMACIÓN BÁSICA CON RELACIONES ESENCIALES
    
    /**
     * Encuentra Accommodation por ID con Ubicación básica
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE a.id = :id")
    Optional<Accomodation> findByIdWithUbication(@Param("id") Long id);
    
    /**
     * Encuentra Accommodation por ID con Fotos
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.urlPhotos " +
           "WHERE a.id = :id")
    Optional<Accomodation> findByIdWithPhotos(@Param("id") Long id);
    
    /**
     * Encuentra Accommodation por ID con Servicios
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.servicesList " +
           "WHERE a.id = :id")
    Optional<Accomodation> findByIdWithServices(@Param("id") Long id);
    
    //INFORMACIÓN COMPUESTA PARA DETALLES
    
    /**
     * Encuentra Accommodation por ID con información completa para página de detalles
     */
    @Query("SELECT DISTINCT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "LEFT JOIN FETCH a.urlPhotos " +
           "LEFT JOIN FETCH a.servicesList " +
           "LEFT JOIN FETCH a.host " +
           "WHERE a.id = :id")
    Optional<Accomodation> findByIdWithCompleteDetails(@Param("id") Long id);
    
    /**
     * Encuentra Accommodation por ID con Calificaciones
     */
    @Query("SELECT DISTINCT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.accomodationCalificationList " +
           "WHERE a.id = :id")
    Optional<Accomodation> findByIdWithCalifications(@Param("id") Long id);
    
    /**
     * Encuentra Accommodation por ID con Comentarios
     */
    @Query("SELECT DISTINCT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.commentary c " +
           "LEFT JOIN FETCH c.author " +
           "WHERE a.id = :id AND c.isVisible = true")
    Optional<Accomodation> findByIdWithVisibleComments(@Param("id") Long id);
    
    // BÚSQUEDAS Y FILTROS PRINCIPALES
    
    /**
     * Encuentra Accommodations por Ciudad
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication u " +
           "LEFT JOIN FETCH a.urlPhotos " +
           "WHERE u.city = :city " +
           "AND a.approvalStatus = 'APPROVED' " +
           "AND a.operationalStatus = 'OPERATIONAL'")
    List<Accomodation> findByCity(@Param("city") Cities city);
    
    /**
     * Encuentra Accommodations por Ciudad con paginación
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication u " +
           "LEFT JOIN FETCH a.urlPhotos " +
           "WHERE u.city = :city " +
           "AND a.approvalStatus = 'APPROVED' " +
           "AND a.operationalStatus = 'OPERATIONAL'")
    Page<Accomodation> findByCity(@Param("city") Cities city, Pageable pageable);
    
    /**
     * Encuentra Accommodations por Host ID
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE a.host.id = :hostId")
    List<Accomodation> findByHostId(@Param("hostId") Long hostId);
    
    // FILTROS AVANZADOS
    
    /**
     * Encuentra Accommodations por capacidad de huéspedes
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication u " +
           "LEFT JOIN FETCH a.urlPhotos " +
           "WHERE a.maxGuestCapacity >= :minGuests " +
           "AND a.approvalStatus = 'APPROVED' " +
           "AND a.operationalStatus = 'OPERATIONAL'")
    List<Accomodation> findByMinGuestCapacity(@Param("minGuests") Integer minGuests);
    
    /**
     * Encuentra Accommodations por tipo
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication u " +
           "LEFT JOIN FETCH a.urlPhotos " +
           "WHERE a.accomodationType = :type " +
           "AND a.approvalStatus = 'APPROVED' " +
           "AND a.operationalStatus = 'OPERATIONAL'")
    List<Accomodation> findByType(@Param("type") AccomodationType type);
    
    /**
     * Búsqueda por texto en título y descripción
     */
    @Query("SELECT a FROM Accomodation a " +
           "LEFT JOIN FETCH a.ubication u " +
           "LEFT JOIN FETCH a.urlPhotos " +
           "WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(a.houseRules) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "AND a.approvalStatus = 'APPROVED' " +
           "AND a.operationalStatus = 'OPERATIONAL'")
    List<Accomodation> searchByText(@Param("searchText") String searchText);
    
    // CONSULTAS DE ESTADO Y APROBACIÓN
    
    /**
     * Encuentra Accommodations por estado de aprobación
     */
    List<Accomodation> findByApprovalStatus(ApprovalStatus approvalStatus);
    
    /**
     * Encuentra Accommodations por estado operacional
     */
    List<Accomodation> findByOperationalStatus(OperationalStatus operationalStatus);
    
    /**
     * Encuentra Accommodations aprobadas y operacionales
     */
    @Query("SELECT a FROM Accomodation a " +
           "WHERE a.approvalStatus = 'APPROVED' " +
           "AND a.operationalStatus = 'OPERATIONAL'")
    List<Accomodation> findApprovedAndOperational();
    
    // ESTADÍSTICAS Y CONTEOS
    
    /**
     * Cuenta Accommodations por Host
     */
    @Query("SELECT COUNT(a) FROM Accomodation a WHERE a.host.id = :hostId")
    Long countByHostId(@Param("hostId") Long hostId);
    
    /**
     * Cuenta Accommodations por estado de aprobación
     */
    Long countByApprovalStatus(ApprovalStatus approvalStatus);
    
    /**
     * Cuenta Accommodations por ciudad
     */
    @Query("SELECT COUNT(a) FROM Accomodation a " +
           "JOIN a.ubication u " +
           "WHERE u.city = :city " +
           "AND a.approvalStatus = 'APPROVED'")
    Long countByCity(@Param("city") Cities city);
    
    // CONSULTAS DE EXISTENCIA Y VALIDACIÓN
    
    /**
     * Verifica si existe Accommodation con título para un Host
     */
    @Query("SELECT COUNT(a) > 0 FROM Accomodation a " +
           "WHERE a.host.id = :hostId AND a.title = :title")
    boolean existsByHostIdAndTitle(@Param("hostId") Long hostId, @Param("title") String title);
    
    /**
     * Verifica si el Host tiene Accommodations activas
     */
    @Query("SELECT COUNT(a) > 0 FROM Accomodation a " +
           "WHERE a.host.id = :hostId " +
           "AND a.approvalStatus = 'APPROVED' " +
           "AND a.operationalStatus = 'OPERATIONAL'")
    boolean hasActiveAccommodations(@Param("hostId") Long hostId);


}
