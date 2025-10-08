package com.gestion.alojamientos.mapper.users;


import com.gestion.alojamientos.dto.admin.CreateAdminDto;
import com.gestion.alojamientos.dto.admin.EditAdminDto;
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
    @Mapping(target = "acces_level", source = "acces_level")
    @Mapping(target = "username", source = "username")
    AdminDto toDTO(Admin admin);

    // ====== DTO → ENTITY ======
    @Mapping(target = "password", ignore = true)     // No se mapea por seguridad
    Admin toEntity(AdminDto adminDto);

    @Mapping(target = "password", ignore = true)     // No se mapea por seguridad
    Admin toEntity(CreateAdminDto adminDto);


    // ====== UPDATE (solo campos específicos) ======
    @Mapping(target = "password", ignore = true) // lo setearemos manualmente
    @Mapping(target = "email", ignore = true)    // el correo no debe cambiar
    @Mapping(target = "id", ignore = true)       // el ID tampoco debe cambiar
    void updateFromDto(EditAdminDto editAdminDto, Admin admin);
}
