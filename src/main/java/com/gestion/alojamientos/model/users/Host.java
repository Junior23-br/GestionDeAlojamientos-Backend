package com.gestion.alojamientos.model.users;

import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.transaction.ServiceFee;
import com.gestion.alojamientos.model.CommentHost;
import com.gestion.alojamientos.model.FinancialAccount;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.base.NormalUser;

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
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un anfitrión en el sistema.
 * Extiende de UserBasic y se mapea a la tabla 'anfitrion'.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "host")
public class Host extends NormalUser {
    /**
     * Estados del Anfitrion: Activo, Inactivo, Suspendido, Eliminado, Pendiente, Aprovado, Rechazado.
     * Descripcion: En src/main/java/com/gestion/alojamientos/model/Enums/StatusOfHost se encuentra la descripcion de cada uno de los estados
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("Estado actual del usuario: Activo, Inactivo, Suspendido, Eliminado" + "\n" + " Pendiente, Aprobdo, Rechazado.")
    private StatesOfHost status;

    @OneToOne
    @JoinColumn(name = "guest_id")
    private Guest guest; // Relación de uno a uno con Guest

    @Column(name = "personal_description", columnDefinition = "TEXT")
    private String personalDescription; // Descripción personal

    @OneToMany(mappedBy = "host")
    private List<Accomodation> listAccommodations; // Relación de uno a muchos con Accomodation
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommentHost> hostCommentList = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "financial_account_id")
    private FinancialAccount receiptPayment; // Cuenta financiera para pagos

    @OneToOne
    @JoinColumn(name = "service_fee_id")
    private ServiceFee serviceFee; // Tarifa de servicio
}