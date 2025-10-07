package com.gestion.alojamientos.mapper.accomodation;

import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.model.accomodation.*;
import org.mapstruct.*;

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
    @InheritInverseConfiguration
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
}

