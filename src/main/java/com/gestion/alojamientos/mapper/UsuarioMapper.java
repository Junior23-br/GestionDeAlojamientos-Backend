package com.gestion.alojamientos.mapper;


import com.gestion.alojamientos.dto.usuario.CrearUsuarioDto;
import com.gestion.alojamientos.dto.usuario.EditarUsuarioDto;
import com.gestion.alojamientos.dto.usuario.UsuarioDto;
import com.gestion.alojamientos.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


/**
*Mapper es el encargado de convertir entre entidades y sus DTO
* Utilizando MapStruct para facilitar la transformacion de datos entre capas
* Este componente esta integrado con Spring mediante componentModel = "spring" y usa mapper auxiliares
*
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
/**
 * Convierte un DTO de creación de usaurio a una entidad de usuario.
 * @param dto datos para crear nuevo usuario
 * @return entidad de usuario mapeada
 */
    @Mapping(target = "user.correo_electronico", source = "correo_electronico")
    @Mapping(target = "user.contrasenia", source = "contrasenia")
    @Mapping(target = "fecha_nacimiento", source = "fecha_nacimiento")
    Usuario toEntity(CrearUsuarioDto dto);
    /**
     * Convierte una entidad de usuario a un DTO
     * @param usuario  entidad de usuario a convertir
     * @return DTO con los datos de usuario
     */
    @Mapping(source ="user.correo_electronico", target = "correo_electronico")
    @Mapping(source = "fecha_nacimiento", target = "fecha_nacimiento")
    UsuarioDto toDto(Usuario usuario);
    /**
     * Actualizar los datos de una entidad de usuario a ppartir de la edicción de un DTO
     */
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "apellido", source = "apellido")
    void updateFromDto(EditarUsuarioDto dto, @MappingTarget Usuario usuario);
}

