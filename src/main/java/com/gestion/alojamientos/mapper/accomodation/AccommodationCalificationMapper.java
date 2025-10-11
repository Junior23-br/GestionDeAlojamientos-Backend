package com.gestion.alojamientos.mapper.accomodation;


import com.gestion.alojamientos.dto.accommodation.CalificationAccommodation.AccommodationCalificationDTO;
import com.gestion.alojamientos.dto.accommodation.CalificationAccommodation.CreateAccommodationCalificationDTO;
import com.gestion.alojamientos.dto.accommodation.CalificationAccommodation.AccommodationCalificationUpdateDTO;
import com.gestion.alojamientos.model.accomodation.AccomodationCalification;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import org.mapstruct.*;

/**
 * Mapper que convierte entre la entidad AccomodationCalification y sus DTOs asociados.
 * Usa MapStruct para realizar el mapeo automático entre objetos.
 *
 * - Se incluyen métodos para crear, actualizar y convertir entidades ↔ DTOs.
 * - Se ignoran las relaciones complejas (Accomodation) al mapear hacia DTOs para evitar
 *   problemas de serialización o recursión.
 */
@Mapper(componentModel = "spring")
public interface AccommodationCalificationMapper {

    // ====== ENTITY → DTO ======
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cleanLiness", source = "cleanliness")
    @Mapping(target = "comfort", source = "comfort")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "accuracyOfListing", source = "accuracyOfListing")
    @Mapping(target = "idAccommodation", source = "accomodation.id")
    @Mapping(target = "comunicationHost", source = "communicationHost")
    @Mapping(target = "prom", source = "prom")
    AccommodationCalificationDTO toDTO(AccomodationCalification calification);


    // ====== DTO (Create) → ENTITY ======
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accomodation", expression = "java(mapAccommodationId(dto.idAccommodation()))")
    @Mapping(target = "cleanliness", source = "cleanLiness")
    @Mapping(target = "comfort", source = "comfort")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "accuracyOfListing", source = "accuractOfListing")
    @Mapping(target = "communicationHost", source = "comunicationHost")
    @Mapping(target = "prom", ignore = true) // se calcula en lógica de servicio
    AccomodationCalification toEntity(CreateAccommodationCalificationDTO dto);


    // ====== DTO (Update) → ENTITY ======
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accomodation", expression = "java(mapAccommodationId(dto.idAccommodation()))")
    @Mapping(target = "cleanliness", source = "cleanLiness")
    @Mapping(target = "comfort", source = "comfort")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "accuracyOfListing", source = "accuractOfListing")
    @Mapping(target = "communicationHost", source = "comunicationHost")
    @Mapping(target = "prom", ignore = true)
    void updateFromDto(AccommodationCalificationUpdateDTO dto, @MappingTarget AccomodationCalification entity);


    // ====== Helper para mapear alojamiento por ID ======
    default Accomodation mapAccommodationId(Long id) {
        if (id == null) return null;
        Accomodation accomodation = new Accomodation();
        accomodation.setId(id);
        return accomodation;
    }
}
