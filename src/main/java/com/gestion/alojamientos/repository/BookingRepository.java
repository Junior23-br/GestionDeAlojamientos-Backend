package com.gestion.alojamientos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.alojamientos.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, String> {

    /**
     * Busca una reserva por su identificador único.
     * @param id Identificador de la reserva.
     * @return Un Optional con la reserva encontrada, o vacío si no existe.
     */
    Optional<Booking> findById(String id);

    /**
     * Busca una reserva por el identificador del alojamiento.
     * @param accomodationId Identificador del alojamiento.
     * @return Un Optional con la reserva encontrada, o vacío si no existe.
     */
    Optional<Booking> findByAccomodationId(String accomodationId);

    /**
     * Busca todas las reservas asociadas a un huésped específico.
     * @param guestId Identificador del huésped.
     * @return Un Optional con la lista de reservas encontradas, o vacío si no existen.
     */
    Optional<List<Booking>> findByGuestId(String guestId);


    /**
     * Verifica si existe una reserva con el identificador dado.
     * @param id Identificador de la reserva.
     * @return true si existe, false en caso contrario.
     */
    boolean existsById(String id);

    /**
     * Verifica si existe una reserva para el alojamiento dado.
     * @param accomodationId Identificador del alojamiento.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByAccomodationId(String accomodationId);

    /**
     * Verifica si existe una reserva para el huésped dado.
     * @param guestId Identificador del huésped.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByGuestId(String guestId);



}
