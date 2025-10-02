package com.gestion.alojamientos.model;

import java.util.Date;

import com.gestion.alojamientos.model.base.SuperUser;

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
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Unique identifier

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private SuperUser sender; // User who sends the message

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private SuperUser receiver; // User who receives the message

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text; // Message text

    @Lob
    @Column(name = "doc")
    private byte[] doc; // Attached document

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", nullable = false)
    private Date createDate; // Creation date

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat; // Associated chat

    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification; // Associated notification
}
