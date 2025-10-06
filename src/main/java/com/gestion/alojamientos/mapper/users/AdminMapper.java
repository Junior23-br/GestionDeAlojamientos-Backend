package com.gestion.alojamientos.mapper.users;


import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.dto.admin.CreateAdminDto;
import com.gestion.alojamientos.dto.admin.EditAdminDto;
import com.gestion.alojamientos.model.users.Admin;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


/**
 * Mapper encargado de convertir entre la entidad Admin y sus DTOs.
 * Utiliza MapStruct para simplificar la transformación de datos entre capas.
 */
// @Mapper(componentModel = "spring")
// public interface AdminMapper {

//     /**
//      * Convierte un DTO de creación de admin a una entidad Admin.
//      * @param dto datos para crear un nuevo admin
//      * @return entidad Admin mapeada
//      */
//     @Mapping(target = "id", source = "id")
//     @Mapping(target = "email", source = "email")
//     @Mapping(target = "password", source = "password")
//     Admin toEntity(CreateAdminDto dto);

//     /**
//      * Convierte una entidad Admin a un DTO.
//      * @param admin entidad de admin a convertir
//      * @return DTO con los datos de admin
//      */
//     @Mapping(target = "id", source = "id")
//     @Mapping(target = "email", source = "email")
//     AdminDto toDto(Admin admin);

//     /**
//      * Actualiza los datos de una entidad Admin a partir de la edición de un DTO.
//      * @param dto datos de edición
//      * @param admin entidad admin a actualizar
//      */
//     @Mapping(target = "id", ignore = true) // nunca se actualiza el ID
//     @Mapping(target = "email", source = "email")
//     @Mapping(target = "password", source = "password")
//     void updateFromDto(EditAdminDto dto, @MappingTarget Admin admin);

// }
