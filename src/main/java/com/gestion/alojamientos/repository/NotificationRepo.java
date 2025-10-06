package com.gestion.alojamientos.repository;

import com.gestion.alojamientos.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepo extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
}
