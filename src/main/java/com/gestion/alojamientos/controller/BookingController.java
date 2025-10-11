package com.gestion.alojamientos.controller;

import com.gestion.alojamientos.dto.booking.BookingCreateDTO;
import com.gestion.alojamientos.dto.booking.BookingUpdateDTO;
import com.gestion.alojamientos.dto.booking.DeleteBookingDTO;
import com.gestion.alojamientos.service.BookingService;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
@Tag(
    name = "Booking",
    description = "Endpoints para la gestión de reservas (Booking). Incluye creación, actualización y eliminación lógica. " +
                  "Requiere autenticación JWT para usuarios con roles GUEST o HOST según el tipo de operación."
)
public class BookingController {
    @Autowired
    private BookingService bookingService;


      // =============================================================
    // ENDPOINT: GET /api/booking/by-guest/{guestId}
    // =============================================================
    @GetMapping("/by-guest/{guestId}")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todas las reservas de un huésped",
        description = "Permite a un usuario con rol **GUEST** consultar todas sus reservas, incluyendo detalles del alojamiento y estado actual."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas del huésped obtenida correctamente.",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingDTO.class)))),
        @ApiResponse(responseCode = "404", description = "No se encontraron reservas o huésped inexistente."),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token inválido."),
        @ApiResponse(responseCode = "403", description = "Rol no autorizado (solo GUEST puede consultar sus reservas)."),
        @ApiResponse(responseCode = "500", description = "Error interno al obtener las reservas.")
    })
    public ResponseEntity<List<BookingDTO>> getBookingsByGuest(
        @Parameter(description = "ID del huésped cuyas reservas se desean consultar.", required = true)
        @PathVariable Long guestId
    ) {
        try {
            List<BookingDTO> bookings = bookingService.getGuestBookings(guestId);
            if (bookings.isEmpty()) {
                return ResponseEntity.status(404).body(Collections.emptyList());
            }
            return ResponseEntity.ok(bookings);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    // =============================================================
    // ENDPOINT: GET /api/booking/by-accommodation/{accommodationId}
    // =============================================================
    @GetMapping("/by-accommodation/{accommodationId}")
    @PreAuthorize("hasRole('ROLE_HOST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todas las reservas de un alojamiento",
        description = "Permite a un usuario con rol **HOST** consultar todas las reservas asociadas a uno de sus alojamientos, incluyendo detalles de huéspedes y fechas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas del alojamiento obtenida correctamente.",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingDTO.class)))),
        @ApiResponse(responseCode = "404", description = "No se encontraron reservas o alojamiento inexistente."),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token inválido."),
        @ApiResponse(responseCode = "403", description = "Rol no autorizado (solo HOST puede consultar reservas de sus alojamientos)."),
        @ApiResponse(responseCode = "500", description = "Error interno al obtener las reservas.")
    })
    public ResponseEntity<List<BookingDTO>> getBookingsByAccommodation(
        @Parameter(description = "ID del alojamiento cuyas reservas se desean consultar.", required = true)
        @PathVariable Long accommodationId
    ) {
        try {
            List<BookingDTO> bookings = bookingService.getAccommodationBookings(accommodationId);
            if (bookings.isEmpty()) {
                return ResponseEntity.status(404).body(Collections.emptyList());
            }
            return ResponseEntity.ok(bookings);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    // ENDPOINT: POST /api/booking/create
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear una nueva reserva (Booking)",
        description = "Permite a un usuario con rol **GUEST** crear una nueva reserva para un alojamiento disponible. " +
                      "Valida disponibilidad, fechas coherentes y políticas del alojamiento antes de confirmar."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o fechas no disponibles."),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token inválido."),
        @ApiResponse(responseCode = "403", description = "Rol no autorizado (solo GUEST puede crear reservas)."),
        @ApiResponse(responseCode = "500", description = "Error interno al procesar la reserva.")
    })
    public ResponseEntity<BookingDTO> createBooking(
        @Parameter(description = "DTO con datos requeridos para la creación de la reserva.", required = true)
        @RequestBody BookingCreateDTO dto
    ) {
        BookingDTO createdBooking = null;
        try {
            createdBooking = bookingService.createBooking(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } 
        return ResponseEntity.status(201).body(createdBooking);
    }

    // ENDPOINT: PUT /api/booking/update
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_HOST')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Actualizar una reserva existente",
        description = "Permite actualizar los datos de una reserva existente. " +
                      "El rol **GUEST** puede modificar fechas antes del check-in, " +
                      "y el **HOST** puede aprobar, rechazar o ajustar detalles según disponibilidad."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva actualizada correctamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o reserva no modificable."),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token inválido."),
        @ApiResponse(responseCode = "403", description = "Rol no autorizado."),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada."),
        @ApiResponse(responseCode = "500", description = "Error interno durante la actualización.")
    })
    public ResponseEntity<BookingDTO> updateBooking(
        @Parameter(description = "DTO con los datos actualizados de la reserva.", required = true)
        @RequestBody BookingUpdateDTO dto
    ) {
        BookingDTO updatedBooking = null;
        try {
            updatedBooking = bookingService.updateBooking(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok().body(updatedBooking);
    }

    // ENDPOINT: DELETE /api/booking/delete
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Eliminar (cancelar) una reserva",
        description = "Permite a un **GUEST** cancelar su propia reserva, o a un **ADMIN** eliminarla del sistema. " +
                      "Se realiza eliminación lógica (estado CANCELLED) y se ajustan los registros de disponibilidad del alojamiento."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva cancelada correctamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDTO.class))),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado o token inválido."),
        @ApiResponse(responseCode = "403", description = "Rol no autorizado."),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada."),
        @ApiResponse(responseCode = "500", description = "Error interno durante la eliminación.")
    })
    public ResponseEntity<Void> deleteBooking(
        @Parameter(description = "DTO con la información necesaria para cancelar o eliminar la reserva.", required = true)
        @RequestBody DeleteBookingDTO dto
    ) {
        boolean cancel;
       try {
        cancel = bookingService.cancelBooking(dto);
       } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(null);
       }
       if (!cancel) {
           return ResponseEntity.status(404).body(null);
       }
       return ResponseEntity.ok(null);
    }
}
