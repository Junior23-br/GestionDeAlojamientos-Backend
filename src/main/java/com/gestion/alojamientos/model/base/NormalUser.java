package com.gestion.alojamientos.model.base;

import java.util.Date;
import java.util.List;

import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.transaction.Transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@MappedSuperclass
public abstract class NormalUser extends SuperUser  {

    @Column(name = "name", nullable = false, length = 100)
    private String name; // Name

    @Column(name = "phone_number", length = 20)
    private String phoneNumber; // Phone number

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    private Date birthDate; // Birth date

    @Column(name = "url_profile_photo", length = 255)
    private String urlProfilePhoto; // URL of profile photo (optional)

    public NormalUser() {
        super();
    }
}
