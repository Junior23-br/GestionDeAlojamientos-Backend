package com.gestion.alojamientos.mapper.users;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.model.users.Admin;

/**
 * Mapper que convierte entre la entidad Admin y su DTO (AdminDto)
 * usando MapStruct.
 *
 * - Se ignora el campo 'password' por motivos de seguridad.
 */
@Mapper(componentModel = "spring")
public interface AdminMapper {

    // ====== ENTITY → DTO ======
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "acces_Level", source = "access_Level")
    AdminDto toDTO(Admin admin);

    // ====== DTO → ENTITY ======
    @Mapping(target = "password", ignore = true)     // No se mapea por seguridad
    Admin toEntity(AdminDto adminDto);
}
