package com.gestion.alojamientos.mapper.accomodation;

import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
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

     /**
     * Actualiza una entidad Accommodation existente con datos del DTO
     * Solo actualiza campos permitidos para edición
     */
    default void updateEntityFromDTO(AccommodationDTO dto, Accomodation entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Campos básicos actualizables
        if (dto.title() != null) {
            entity.setTitle(dto.title());
        }
        
        if (dto.accomodationType() != null) {
            try {
                AccomodationType type = AccomodationType.valueOf(dto.accomodationType().toUpperCase());
                entity.setAccomodationType(type);
            } catch (IllegalArgumentException e) {
                // Log warning pero no falla la actualización
                // El tipo permanece sin cambios
            }
        }
        
        if (dto.houseRules() != null) {
            entity.setHouseRules(dto.houseRules());
        }
        
        if (dto.maxGuestCapacity() != null) {
            entity.setMaxGuestCapacity(dto.maxGuestCapacity());
        }
        
        if (dto.numberOfBeds() != null) {
            entity.setNumberOfBeds(dto.numberOfBeds());
        }
        
        if (dto.numberOfBathrooms() != null) {
            entity.setNumberOfBathrooms(dto.numberOfBathrooms());
        }
        
        if (dto.urlPhotos() != null) {
            entity.setUrlPhotos(new ArrayList<>(dto.urlPhotos()));
        }

        // Timestamp de actualización
        entity.setUpdateTime(LocalDateTime.now());

        // NOTA: Los siguientes campos NO se actualizan automáticamente:
        // - host (relación fija una vez creado)
        // - ubication (requiere lógica específica de validación)
        // - servicesList (se actualiza por separado en el servicio)
        // - approvalStatus, operationalStatus (controlados por admin/sistema)
        // - createdTime (inmutable)
        // - Relaciones: bookingList, commentary, accomodationCalificationList
    }

    /**
     * Método helper para mapear lista de IDs de servicios a entidades Services
     */
    default List<Services> mapServiceIdsToEntities(List<Long> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return new ArrayList<>();
        }
        // Este método se usa en el servicio para cargar las entidades Services
        // La implementación real requiere inyección de ServicesRepo
        // Por ahora retornamos lista vacía - se implementará en el servicio
        return new ArrayList<>();
    }

    /**
     * Método helper para mapear lista de entidades Services a IDs
     */
    default List<Long> mapServicesToIds(List<Services> services) {
        if (services == null || services.isEmpty()) {
            return new ArrayList<>();
        }
        return services.stream()
                .map(Services::getId)
                .collect(Collectors.toList());
    }

    /**
     * Conversión de String a AccommodationType con manejo de errores
     */
    default AccomodationType mapAccommodationType(String accommodationType) {
        if (accommodationType == null) {
            return null;
        }
        try {
            return AccomodationType.valueOf(accommodationType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // O un valor por defecto según tu lógica de negocio
        }
    }

    /**
     * Conversión de AccommodationType a String
     */
    default String mapAccommodationTypeToString(AccomodationType accommodationType) {
        return accommodationType != null ? accommodationType.name() : null;
    }
}
