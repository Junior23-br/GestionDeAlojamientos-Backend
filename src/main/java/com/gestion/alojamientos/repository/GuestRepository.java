package com.gestion.alojamientos.repository;

import com.gestion.alojamientos.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
*Repositorio para gestionar operaciones de acceso a datos sobre la entidad, fundamental en la capa de persistencia
* Extiende de JPA para proporcionar metodos CRUD basicos(guardar,buscar,editar,eliminar)
* JPASpecificationExecutor permite ejecutar consultas avanzadas y dinamicas usando la API de especificaciones de JPA,
* ideal para filtros personalizados
 */
@Repository
public interface GuestRepository extends JpaRepository<Guest, Long>, JpaSpecificationExecutor<Guest> {

    /**
    *Buscar un huesped a partir del correo registrado
     */
    Optional<Guest> findByEmail(String email);
    /**
    *Buscar un huesped por su numero de phoneNumber
     */
    Optional<Guest> findByPhoneNumber(String phoneNumber);
    /**
    *Buscar huesped por su firstName
     */
    Optional<Guest> findByFirstName(String firstName);
    /**
    *Buscar un huesped por su lastName
     */
    Optional<Guest> findByLastName(String lastName);
    /**
     *Buscar huesped por su id
     */
    Optional<Guest> findById(Long id);

    /**
     * Verifica si existe un huesped con el correo especificado
     * @param email correo electronico a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);
;
    /**
     * Verifica si un huesped existe con el numero de phoneNumber especificado
     * @param phoneNumber numero telefonico a verificar
     * @return true si existe, falso si no existe
     */
   boolean existsByPhoneNumber(String phoneNumber);
}
