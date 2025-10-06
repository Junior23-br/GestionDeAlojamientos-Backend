package com.gestion.alojamientos.repository.accomodation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.accomodation.CommentAccomodation;

@Repository
public interface CommentAccomodationRepo extends JpaRepository<CommentAccomodation, Long> {
    
    /**
     * Encuentra comentarios por Accommodation ID
     */
    @Query("SELECT c FROM CommentAccomodation c " +
           "LEFT JOIN FETCH c.author " +
           "LEFT JOIN FETCH c.respondeHost " +
           "WHERE c.accomodation.id = :accommodationId " +
           "AND c.isVisible = true " +
           "ORDER BY c.creationDate DESC")
    List<CommentAccomodation> findByAccommodationId(@Param("accommodationId") Long accommodationId);
    
    /**
     * Encuentra comentarios por Accommodation ID paginados
     */
    @Query("SELECT c FROM CommentAccomodation c " +
           "LEFT JOIN FETCH c.author " +
           "WHERE c.accomodation.id = :accommodationId " +
           "AND c.isVisible = true")
    Page<CommentAccomodation> findByAccommodationId(@Param("accommodationId") Long accommodationId, Pageable pageable);
    
    /**
     * Encuentra comentarios por autor
     */
    @Query("SELECT c FROM CommentAccomodation c " +
           "LEFT JOIN FETCH c.accomodation " +
           "WHERE c.author.id = :authorId " +
           "AND c.isVisible = true")
    List<CommentAccomodation> findByAuthorId(@Param("authorId") Long authorId);
    
    /**
     * Cuenta comentarios visibles por Accommodation
     */
    @Query("SELECT COUNT(c) FROM CommentAccomodation c " +
           "WHERE c.accomodation.id = :accommodationId " +
           "AND c.isVisible = true")
    Long countVisibleCommentsByAccommodationId(@Param("accommodationId") Long accommodationId);
}