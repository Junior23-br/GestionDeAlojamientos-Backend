package com.gestion.alojamientos.mapper;


import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.model.base.NormalUser;
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
    @Mapping(target = "email", source = "source.email")
    @Mapping(target = "password", source = "source.password")
    UserLoginDTO toLoginDTO(NormalUser source);
}
