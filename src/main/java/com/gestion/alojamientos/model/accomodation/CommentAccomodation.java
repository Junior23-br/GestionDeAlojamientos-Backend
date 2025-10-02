package com.gestion.alojamientos.model.accomodation;

import java.time.LocalDateTime;

import com.gestion.alojamientos.model.base.NormalUser;
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
@Table(name = "comment_accomodation")
public class CommentAccomodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Identificador único

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text; // Texto del comentario

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate; // Fecha de creación

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Guest author; // Autor del comentario

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible; // Visibilidad

    @ManyToOne
    @JoinColumn(name = "responde_host_id")
    private CommentAccomodation respondeHost; // Respuesta del anfitrión

    @ManyToOne
    @JoinColumn(name = "accomodation_id")
    private Accomodation accomodation; // Alojamiento relacionado
}
