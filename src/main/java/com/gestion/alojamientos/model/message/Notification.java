package com.gestion.alojamientos.model.message;

import java.time.LocalDateTime;

import com.gestion.alojamientos.model.enums.ClasificationNotification;
import com.gestion.alojamientos.model.users.Guest;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Unique identifier

    @Column(name = "is_read", nullable = false)
    private boolean read; // Indicates if the notification was read

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate; // Creation date

    @Column(name = "title", nullable = false, length = 255)
    private String title; // Notification title

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message; // Notification message

    @Enumerated(EnumType.STRING)
    @Column(name = "clasification_notification", nullable = false, length = 50)
    private ClasificationNotification clasificationNotification; // Notification classification

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Guest receiver;
}