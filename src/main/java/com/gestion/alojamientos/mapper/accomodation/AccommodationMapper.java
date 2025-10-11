package com.gestion.alojamientos.mapper.accomodation;

import com.gestion.alojamientos.dto.accommodation.AccommodationCreateDTO;
import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.dto.accommodation.AccommodationUpdateDTO;
import com.gestion.alojamientos.dto.accommodation.DeleteAccommodationDTO;
import com.gestion.alojamientos.model.accomodation.*;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de MapStruct para convertir entre la entidad Accomodation y su DTO AccommodationDTO.
 */
@Mapper(componentModel = "spring")
public interface AccommodationMapper {

    // ======= ENTITY → DTO =======
    @Mapping(target = "accomodationType", source = "accomodationType", qualifiedByName = "enumToString")
    @Mapping(target = "approvalStatus", source = "approvalStatus", qualifiedByName = "enumToString")
    @Mapping(target = "operationalStatus", source = "operationalStatus", qualifiedByName = "enumToString")
    @Mapping(target = "ubicationID", source = "ubication.id")
    @Mapping(target = "hostID", source = "host.id")
    @Mapping(target = "bookingsID", source = "bookingList", qualifiedByName = "mapBookingListToIds")
    @Mapping(target = "accommodationCalificationsIDs", source = "accomodationCalificationList", qualifiedByName = "mapCalificationListToIds")
    @Mapping(target = "commentaryIDs", source = "commentary", qualifiedByName = "mapCommentaryListToIds")
    @Mapping(target = "services", source = "servicesList")
    @Mapping(target = "updatedTime", source = "updateTime")
    AccommodationDTO toDto(Accomodation entity);

    // ======= DTO → ENTITY =======
    @Mapping(target = "ubication", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    @Mapping(target = "commentary", ignore = true)
    @Mapping(target = "accomodationCalificationList", ignore = true)
    @Mapping(target = "urlPhotos", ignore = true)
    @Mapping(target = "servicesList", source = "services") // si son el mismo tipo
    @Mapping(target = "accomodationType", ignore = true) // porque viene como String
    @Mapping(target = "approvalStatus", ignore = true)
    @Mapping(target = "operationalStatus", ignore = true)
    Accomodation toEntity(AccommodationDTO dto);


      // ======= CREATE DTO → ENTITY =======
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ubication", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    @Mapping(target = "commentary", ignore = true)
    @Mapping(target = "accomodationCalificationList", ignore = true)
    @Mapping(target = "approvalStatus", ignore = true)
    @Mapping(target = "operationalStatus", ignore = true)
    @Mapping(target = "servicesList", ignore = true)
    @Mapping(target = "accomodationType", expression = "java(mapAccommodationType(dto.accommodationType()))")
    @Mapping(target = "urlPhotos", source = "urlPhotos")
    @Mapping(target = "createdTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    Accomodation toEntity(AccommodationCreateDTO dto);


     // ======= UPDATE DTO → ENTITY (solo campos actualizables) =======
    @Mapping(target = "ubication", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    @Mapping(target = "commentary", ignore = true)
    @Mapping(target = "accomodationCalificationList", ignore = true)
    @Mapping(target = "approvalStatus", ignore = true)
    @Mapping(target = "operationalStatus", ignore = true)
    @Mapping(target = "servicesList", ignore = true)
    @Mapping(target = "accomodationType", expression = "java(mapAccommodationType(dto.accommodationType()))")
    @Mapping(target = "urlPhotos", source = "urlPhotos")
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    Accomodation toEntity(AccommodationUpdateDTO dto);

    // ======= DELETE DTO → ENTITY (solo para validación o soft delete) =======
    @Mapping(target = "id", source = "idAccommodation")
    @Mapping(target = "host.id", source = "idHost")
    @Mapping(target = "approvalStatus", ignore = true)
    @Mapping(target = "operationalStatus", ignore = true)
    @Mapping(target = "ubication", ignore = true)
    @Mapping(target = "servicesList", ignore = true)
    @Mapping(target = "bookingList", ignore = true)
    @Mapping(target = "commentary", ignore = true)
    @Mapping(target = "accomodationCalificationList", ignore = true)
    @Mapping(target = "accomodationType", ignore = true)
    @Mapping(target = "urlPhotos", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    Accomodation toEntity(DeleteAccommodationDTO dto);
    // ======= MÉTODOS AUXILIARES =======
    @Named("enumToString")
    default String enumToString(Enum<?> e) {
        return e != null ? e.name() : null;
    }

    @Named("mapBookingListToIds")
    default List<Long> mapBookingListToIds(List<com.gestion.alojamientos.model.booking.Booking> bookings) {
        if (bookings == null) return null;
        return bookings.stream()
                .map(com.gestion.alojamientos.model.booking.Booking::getId)
                .collect(Collectors.toList());
    }

    @Named("mapCalificationListToIds")
    default List<Long> mapCalificationListToIds(List<AccomodationCalification> califications) {
        if (califications == null) return null;
        return califications.stream()
                .map(AccomodationCalification::getId)
                .collect(Collectors.toList());
    }

    @Named("mapCommentaryListToIds")
    default List<Long> mapCommentaryListToIds(List<CommentAccomodation> comments) {
        if (comments == null) return null;
        return comments.stream()
                .map(CommentAccomodation::getId)
                .collect(Collectors.toList());
    }

    default AccomodationType mapAccommodationType(String accommodationType) {
        if (accommodationType == null) return null;
        try {
            return AccomodationType.valueOf(accommodationType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default String mapAccommodationTypeToString(AccomodationType accommodationType) {
        return accommodationType != null ? accommodationType.name() : null;
    }

    default List<Long> mapServicesToIds(List<Services> services) {
        if (services == null || services.isEmpty()) return new ArrayList<>();
        return services.stream().map(Services::getId).collect(Collectors.toList());
    }

    default List<Services> mapServiceIdsToEntities(List<Long> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) return new ArrayList<>();
        return new ArrayList<>();
    }

    // ======= ACTUALIZACIÓN PARCIAL =======
    default void updateEntityFromDTO(AccommodationUpdateDTO dto, Accomodation entity) {
        if (dto == null || entity == null) return;

        if (dto.title() != null) entity.setTitle(dto.title());
        if (dto.accommodationType() != null) {
            try {
                entity.setAccomodationType(AccomodationType.valueOf(dto.accommodationType().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (dto.houseRules() != null) entity.setHouseRules(dto.houseRules());
        if (dto.maxGuestCapacity() != null) entity.setMaxGuestCapacity(dto.maxGuestCapacity());
        if (dto.numberOfBeds() != null) entity.setNumberOfBeds(dto.numberOfBeds());
        if (dto.numberOfBathrooms() != null) entity.setNumberOfBathrooms(dto.numberOfBathrooms());
        if (dto.urlPhotos() != null) entity.setUrlPhotos(new ArrayList<>(dto.urlPhotos()));
        entity.setUpdateTime(LocalDateTime.now());
    }
}
