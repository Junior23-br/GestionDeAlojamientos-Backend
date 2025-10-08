package com.gestion.alojamientos.repository.user;

import com.gestion.alojamientos.model.users.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {

     /**
      * Buscar un administrador por correo electrónico
      */
     @Query("SELECT a FROM Admin a where a.email =?")
     Optional<Admin> findByEmail(String email);


     /**
      * Buscar un administrador por identificador
      */
     @Query("SELECT a FROM Admin a where a.id =?")
     Optional<Admin> findById(Long id);


     /**
      * Verificar si existe un administrador con el correo especificado
      * @param email correo electrónico
      * @return true si existe, falso si no
      */
     boolean existsByEmail(String email);
}
