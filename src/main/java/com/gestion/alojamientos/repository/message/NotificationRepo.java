package com.gestion.alojamientos.repository.message;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gestion.alojamientos.model.enums.ClasificationNotification;
import com.gestion.alojamientos.model.message.Notification;

public interface NotificationRepo extends CrudRepository<Notification, Long> {
    
    // CONSULTAS BÁSICAS
    
    /**
     * Encuentra notificaciones por receptor
     */
    List<Notification> findByReceiverIdOrderByCreatedDateDesc(Long receiverId);
    
    /**
     * Encuentra notificaciones no leídas por receptor
     */
    List<Notification> findByReceiverIdAndReadFalseOrderByCreatedDateDesc(Long receiverId);
    
    /**
     * Cuenta notificaciones no leídas por receptor
     */
    Long countByReceiverIdAndReadFalse(Long receiverId);
    
    /**
     * Encuentra notificaciones por clasificación
     */
    List<Notification> findByReceiverIdAndClasificationNotificationOrderByCreatedDateDesc(
        Long receiverId, ClasificationNotification classification);
    
    // CONSULTAS DE EXISTENCIA
    boolean existsByReceiverIdAndReadFalse(Long receiverId);
    
    // OPERACIONES DE ACTUALIZACIÓN
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.receiver.id = :receiverId AND n.read = false")
    int markAllAsReadByReceiver(@Param("receiverId") Long receiverId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :notificationId")
    int markAsRead(@Param("notificationId") Long notificationId);
}
