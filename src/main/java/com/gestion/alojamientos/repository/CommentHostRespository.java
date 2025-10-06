package com.gestion.alojamientos.repository;

import com.gestion.alojamientos.model.CommentHost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommentHostRespository extends JpaRepository<CommentHost, Long>, JpaSpecificationExecutor<CommentHost> {
    
}
