package com.gestion.alojamientos.controller;

import com.gestion.alojamientos.service.BookingNotificationScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador REST para gestión del scheduler de notificaciones de check-in.
 * Proporciona endpoints para controlar y monitorear el sistema de notificaciones automáticas.
 */
@RestController
@RequestMapping("/api/scheduler")
@Tag(name = "Notification Scheduler", description = "API para gestión del scheduler de notificaciones")
public class SchedulerController {

    private final BookingNotificationScheduler bookingNotificationScheduler;

    public SchedulerController(BookingNotificationScheduler bookingNotificationScheduler) {
        this.bookingNotificationScheduler = bookingNotificationScheduler;
    }

    /**
     * Obtiene el estado actual del scheduler.
     * 
     * @return Información del estado del scheduler
     */
    @GetMapping("/status")
    @Operation(summary = "Estado del scheduler", 
               description = "Obtiene el estado actual del scheduler de notificaciones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente")
    })
    public ResponseEntity<String> getSchedulerStatus() {
        String status = bookingNotificationScheduler.getSchedulerStatus();
        return ResponseEntity.ok(status);
    }

    /**
     * Envía recordatorios de check-in para una fecha específica.
     * 
     * @param targetDate Fecha objetivo para buscar reservas
     * @return Número de recordatorios enviados
     */
    @PostMapping("/send-reminders")
    @Operation(summary = "Enviar recordatorios manuales", 
               description = "Envía recordatorios de check-in para una fecha específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recordatorios enviados exitosamente")
    })
    public ResponseEntity<Integer> sendCheckInRemindersForDate(
            @Parameter(description = "Fecha objetivo para buscar reservas") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        
        int sentCount = bookingNotificationScheduler.sendCheckInRemindersForDate(targetDate);
        return ResponseEntity.ok(sentCount);
    }

    /**
     * Envía recordatorios de check-in para mañana (próximo día).
     * 
     * @return Número de recordatorios enviados
     */
    @PostMapping("/send-reminders/tomorrow")
    @Operation(summary = "Enviar recordatorios para mañana", 
               description = "Envía recordatorios de check-in para el próximo día")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recordatorios enviados exitosamente")
    })
    public ResponseEntity<Integer> sendCheckInRemindersForTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        int sentCount = bookingNotificationScheduler.sendCheckInRemindersForDate(tomorrow);
        return ResponseEntity.ok(sentCount);
    }
}
