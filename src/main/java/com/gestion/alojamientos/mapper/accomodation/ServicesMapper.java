package com.gestion.alojamientos.mapper.accomodation;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gestion.alojamientos.dto.accommodation.ServiceDTO;
import com.gestion.alojamientos.model.accomodation.Services;

@Mapper(componentModel = "spring")
public interface ServicesMapper {

    // ======= ENTITY → DTO =======
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ServiceDTO toDto(Services entity);

    // ======= DTO → ENTITY =======
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Services toEntity(ServiceDTO dto);

    // ======= LISTAS =======
    List<ServiceDTO> toDtoList(List<Services> entities);

    List<Services> toEntityList(List<ServiceDTO> dtos);    
}
