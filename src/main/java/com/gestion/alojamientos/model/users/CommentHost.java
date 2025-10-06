package com.gestion.alojamientos.model.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments_host")
public class CommentHost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Relación Many-to-One con el usuario que envía el comentario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Guest sender;

    // Relación Many-to-One con el usuario que recibe el comentario (Host)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Host receiver;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

}
