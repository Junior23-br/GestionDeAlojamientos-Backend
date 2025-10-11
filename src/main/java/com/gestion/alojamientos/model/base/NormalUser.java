package com.gestion.alojamientos.model.base;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.transaction.Transaction;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@MappedSuperclass
@SuperBuilder
@EqualsAndHashCode(callSuper = true)

public abstract class NormalUser extends SuperUser  {

    @Column(name = "name", nullable = false, length = 100)
    @Comment("Nombre de la persona")
    private String name; // Name

    @Column(name = "phone_number", nullable = false)
    @Comment("Número telefónico de contacto")
    private String phoneNumber; // Phone number

    @Column(name = "birth_date")
    @Comment("Fecha de nacimiento de la persona")
    private LocalDate birthDate; // Birth date

    @Column(name = "url_profile_photo", length = 255)
    @Comment("Foto de perfil de la persona")
    private String urlProfilePhoto; // URL of profile photo (optional)
}
