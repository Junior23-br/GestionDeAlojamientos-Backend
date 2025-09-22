package com.gestion.alojamientos.repository;

import com.gestion.alojamientos.model.Usuario;
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
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    /**
    *Buscar un usuario a partir del correo registrado
     */
    Optional<Usuario> findByUser_CorreoElectronico(String correoElectronico);
    /**
    *Buscar un usuario por su numero de telefono
     */
    Optional<Usuario> findByTelefono(String telefono);
    /**
    *Buscar usuario por su nombre
     */
    Optional<Usuario> findByNombre(String nombre);
    /**
    *Buscar un usuario por su apellido
     */
    Optional<Usuario> findByApellido(String apellido);
    /**
     *Buscar usuario por su id
     */
    Optional<Usuario> findById(Long id);

    /**
     * Verifica si existe un usuario con el correo especificado
     * @param correoElectronico correo electronico a verificar
     * @return true si existe
     */
    boolean existsByUser_CorreoElectronico(String correoElectronico);
;
    /**
     * Verifica si un usuario existe con el numero de telefono especificado
     * @param telefono numero telefonico a verificar
     * @return true si existe, falso si no existe
     */
   boolean existsByTelefono(String telefono);
}
