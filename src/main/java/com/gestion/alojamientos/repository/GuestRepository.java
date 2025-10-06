package com.gestion.alojamientos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.users.Guest;

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
      * Busca un huésped a partir del correo registrado.
      * @param email correo del huésped a buscar
      * @return un Optional con el huésped si existe, vacío en caso contrario
      */
     Optional<Guest> findByEmail(String email);
     /**
     *Buscar un huesped por su numero de phoneNumber
      *@param phoneNumber  telefono del huesped a buscar
      * @return un Optional con el huésped si existe, vacío en caso contrario
      */
     Optional<Guest> findByPhoneNumber(String phoneNumber);
     /**
     *Buscar huesped por su firstName
      * @param firstName nombre del huesped
      * @return un Optional con el huésped si existe, vacío en caso contrario
      */
     Optional<Guest> findByFirstName(String firstName);
     /**
     *Buscar un huesped por su lastName
      * @param lastName apellido del huesped
      * @return un Optional con el huésped si existe, vacío en caso contrario
      */
     Optional<Guest> findByLastName(String lastName);
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
