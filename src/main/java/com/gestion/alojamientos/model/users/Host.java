package com.gestion.alojamientos.model.users;

import com.gestion.alojamientos.model.enums.Role;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.message.CommentHost;
import com.gestion.alojamientos.model.transaction.FinancialAccount;
import com.gestion.alojamientos.model.transaction.ServiceFee;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.base.NormalUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un anfitrión en el sistema.
 * Extiende de UserBasic y se mapea a la tabla 'anfitrion'.
 */
@Data
@SuperBuilder
@Entity
@Table(name = "host")
@EqualsAndHashCode(callSuper = true)
public class Host extends NormalUser {
    /**
     * Estados del Anfitrion: Activo, Inactivo, Suspendido, Eliminado, Pendiente, Aprovado, Rechazado.
     * Descripcion: En src/main/java/com/gestion/alojamientos/model/Enums/StatusOfHost se encuentra la descripcion de cada uno de los estados
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("Estado actual del usuario: Activo, Inactivo, Suspendido, Eliminado" + "\n" + " Pendiente, Aprobdo, Rechazado.")
    private StatesOfHost status;

    @Column(name = "personal_description", columnDefinition = "TEXT")
    private String personalDescription; // Descripción personal

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Accomodation> listAccommodations = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommentHost> hostCommentList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "financial_account_id")
    private FinancialAccount receiptPayment;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "service_fee_id")
    private ServiceFee serviceFee;

    /**
     * Role of the user: GUEST or HOST.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private Role role;


    public Host() {

    }
}