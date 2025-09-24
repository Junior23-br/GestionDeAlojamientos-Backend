package com.gestion.alojamientos.mapper;
import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.model.Guest;
import org.mapstruct.InheritConfiguration;
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
public interface GuestMapper {
/**
 * Convierte un DTO de creación de huesped a una entidad de usuario.
 * @param dto datos para crear nuevo huesped
 * @return entidad de huesped mapeada
 */
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    Guest toEntity(CreateGuestDto dto);
    /**
     * Convierte una entidad de huesped a un DTO
     * @param dto  entidad de huesped a convertir
     * @return DTO con los datos de huesped
     */
    @Mapping(source = "email", target = "email")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    GuestDto toDto(Guest dto);
    /**
     * Actualizar los datos de una entidad de huesped a ppartir de la edicción de un DTO
     */
    @InheritConfiguration
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    void updateFromDto(EditGuestDto dto, @MappingTarget Guest guest);
}

