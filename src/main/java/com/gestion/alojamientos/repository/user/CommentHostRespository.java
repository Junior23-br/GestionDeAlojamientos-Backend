package com.gestion.alojamientos.repository.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestion.alojamientos.model.users.CommentHost;

public interface CommentHostRespository extends JpaRepository<CommentHost, Long>, JpaSpecificationExecutor<CommentHost> {
    /**
     * Encuentra Comentarios por Host ID con información del sender
     */
    @Query("SELECT c FROM CommentHost c " +
           "JOIN FETCH c.sender " +
           "WHERE c.receiver.id = :hostId " +
           "ORDER BY c.id DESC")
    List<CommentHost> findByHostId(@Param("hostId") Long hostId);
    
    /**
     * Encuentra Comentarios paginados por Host ID
     */
    @Query("SELECT c FROM CommentHost c " +
           "JOIN FETCH c.sender " +
           "WHERE c.receiver.id = :hostId")
    Page<CommentHost> findByHostId(@Param("hostId") Long hostId, Pageable pageable);
}
