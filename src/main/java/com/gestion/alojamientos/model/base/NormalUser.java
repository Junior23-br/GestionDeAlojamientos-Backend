package com.gestion.alojamientos.model.base;

import java.util.Date;
import java.util.List;

import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.transaction.Transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@AllArgsConstructor
@Getter
@Setter
@MappedSuperclass
@SuperBuilder
public abstract class NormalUser extends SuperUser  {

    @Column(name = "name", nullable = false, length = 100)
    @Comment("Nombre de la persona")
    private String name; // Name

    @Column(name = "phone_number", length = 10, nullable = false)
    @Comment("Número telefónico de contacto")
    private String phoneNumber; // Phone number

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    @Comment("Fecha de nacimiento de la persona")
    private Date birthDate; // Birth date

    @Column(name = "url_profile_photo", length = 255)
    @Comment("Foto de perfil de la persona")
    private String urlProfilePhoto; // URL of profile photo (optional)

    public NormalUser() {
        super();
    }
}
