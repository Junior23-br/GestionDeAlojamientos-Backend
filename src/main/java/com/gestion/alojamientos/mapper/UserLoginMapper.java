package com.gestion.alojamientos.mapper;


import com.gestion.alojamientos.dto.UserLoginDTO;
import org.mapstruct.*;

/**
 * Mapper para convertir diferentes DTOs o entidades de usuario a UserLoginDTO.
 * Solo mapea los campos email y password, ignorando todos los demás.
 */
@Mapper(componentModel = "spring")
public interface UserLoginMapper {

    /**
     * Convierte cualquier objeto fuente (por ejemplo, un UserDTO o entidad) en un UserLoginDTO,
     * mapeando únicamente los campos email y password.
     */
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    UserLoginDTO toLoginDTO(Object source);
}
