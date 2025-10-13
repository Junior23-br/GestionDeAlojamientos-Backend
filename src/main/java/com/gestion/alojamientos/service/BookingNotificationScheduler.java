package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.model.booking.Booking;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio programado para enviar notificaciones automáticas de check-in.
 * Ejecuta tareas programadas para enviar recordatorios 24 horas antes del check-in.
 */
public interface BookingNotificationScheduler {

    /**
     * Tarea programada que se ejecuta cada hora para enviar notificaciones de check-in.
     * Busca reservas cuyo check-in ocurra dentro de las próximas 24 horas y envía correos
     * tanto al huésped como al host.
     */
    void sendCheckInReminders();

    /**
     * Método manual para enviar recordatorios de check-in para una fecha específica.
     * Útil para testing o envío manual de notificaciones.
     *
     * @param targetDate Fecha objetivo para buscar reservas
     * @return Número de recordatorios enviados
     */
    int sendCheckInRemindersForDate(LocalDate targetDate);

    /**
     * Verifica el estado del scheduler y muestra estadísticas.
     *
     * @return Información del estado actual
     */
    String getSchedulerStatus();
}