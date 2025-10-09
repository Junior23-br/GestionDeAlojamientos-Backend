package com.gestion.alojamientos.repository.user;

import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Host;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface HostRepo extends JpaRepository<Host, Long>, JpaSpecificationExecutor<Host> {
    
    // INFORMACIÓN BÁSICA DEL HOST CON RELACIONES ESENCIALES
    
    /**
     * Encuentra Host por ID con Accommodations básicas
     */
    @Query("SELECT DISTINCT h FROM Host h " +
           "LEFT JOIN FETCH h.listAccommodations " +
           "WHERE h.id = :hostId")
    Optional<Host> findByIdWithAccommodations(@Param("hostId") Long hostId);
    
    /**
     * Encuentra Host por ID con Accommodations y Ubicaciones
     */
    @Query("SELECT DISTINCT h FROM Host h " +
           "LEFT JOIN FETCH h.listAccommodations a " +
           "LEFT JOIN FETCH a.ubication " +
           "WHERE h.id = :hostId")
    Optional<Host> findByIdWithAccommodationsAndUbication(@Param("hostId") Long hostId);
    
    /**
     * Encuentra Host por ID con Información Financiera
     */
    @Query("SELECT DISTINCT h FROM Host h " +
           "LEFT JOIN FETCH h.receiptPayment " +
           "LEFT JOIN FETCH h.serviceFee " +
           "WHERE h.id = :hostId")
    Optional<Host> findByIdWithFinancialInfo(@Param("hostId") Long hostId);
    
    /**
     * Encuentra Host por ID con Comentarios
     */
    @Query("SELECT DISTINCT h FROM Host h " +
           "LEFT JOIN FETCH h.hostCommentList c " +
           "LEFT JOIN FETCH c.sender " +
           "WHERE h.id = :hostId")
    Optional<Host> findByIdWithComments(@Param("hostId") Long hostId);
    
    // INFORMACIÓN COMPLETA DEL HOST (TODAS LAS RELACIONES)
    
    /**
     * Encuentra Host por ID con toda la información compuesta
     */
    @Query("SELECT DISTINCT h FROM Host h " +
           "LEFT JOIN FETCH h.listAccommodations a " +
           "LEFT JOIN FETCH a.ubication " +
           "LEFT JOIN FETCH h.hostCommentList c " +
           "LEFT JOIN FETCH c.sender " +
           "LEFT JOIN FETCH h.receiptPayment " +
           "LEFT JOIN FETCH h.serviceFee " +
           "WHERE h.id = :hostId")
    Optional<Host> findByIdWithAllDetails(@Param("hostId") Long hostId);
    
    // CONSULTAS PARA DASHBOARD/ESTADÍSTICAS
    
    /**
     * Encuentra Hosts por Estado con Accommodations count
     */
    @Query("SELECT h, COUNT(a) as accommodationCount FROM Host h " +
           "LEFT JOIN h.listAccommodations a " +
           "WHERE h.status = :status " +
           "GROUP BY h")
    Page<Object[]> findByStatusWithAccommodationCount(@Param("status") StatesOfHost status, 
                                                     Pageable pageable);
    
    /**
     * Cuenta Accommodations por Host
     */
    @Query("SELECT COUNT(a) FROM Accomodation a WHERE a.host.id = :hostId")
    Long countAccommodationsByHostId(@Param("hostId") Long hostId);
    
    /**
     * Cuenta Comentarios por Host
     */
    @Query("SELECT COUNT(c) FROM CommentHost c WHERE c.receiver.id = :hostId")
    Long countCommentsByHostId(@Param("hostId") Long hostId);
    
    // CONSULTAS ESPECÍFICAS PARA BUSINESS LOGIC
    
    /**
     * Encuentra Hosts activos con Accommodations aprobadas
     */
    @Query("SELECT DISTINCT h FROM Host h " +
           "JOIN FETCH h.listAccommodations a " +
           "WHERE h.status = 'ACTIVO' " +
           "AND a.approvalStatus = 'APPROVED' " +
           "AND a.operationalStatus = 'OPERATIONAL'")
    List<Host> findActiveHostsWithOperationalAccommodations();
    
    /**
     * Encuentra Host por ID con Accommodations activas solamente
     */
    @Query("SELECT DISTINCT h FROM Host h " +
           "LEFT JOIN FETCH h.listAccommodations a " +
           "WHERE h.id = :hostId " +
           "AND (a IS NULL OR a.approvalStatus = 'APPROVED')")
    Optional<Host> findByIdWithActiveAccommodationsOnly(@Param("hostId") Long hostId);
    
    // CONSULTAS DE EXISTENCIA Y VALIDACIÓN
    
    /**
     * Verifica si existe un Host con email
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe Accommodation con título para un Host
     */
    @Query("SELECT COUNT(a) > 0 FROM Accomodation a " +
           "WHERE a.host.id = :hostId AND a.title = :title")
    boolean existsAccommodationWithTitle(@Param("hostId") Long hostId, 
                                        @Param("title") String title);


    /**
     * Busca un host por identificador
     */
    @Query("select a from Host a where a.id = :hostId ")
    Optional<Host> findByHostId(@Param("hostId") Long hostId);

    /**
     * Encuentra un Host por el correo electronico
     */
    @Query("select a from Host a where a.email= :email")
    Optional<Host> findByEmail(@Param("email") String email);


}

