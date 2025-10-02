package com.gestion.alojamientos.model.users;

import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

import com.gestion.alojamientos.model.FinancialAccount;
import com.gestion.alojamientos.model.base.NormalUser;
import com.gestion.alojamientos.model.booking.Booking;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a guest in the system.
 * Extends NormalUser and maps to the 'guest' table.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "guest")
public class Guest extends NormalUser {
    /**
     * Guest states: Active, Deleted, Suspended, Inactive.
     * Description: See src/main/java/com/gestion/alojamientos/model/enums/StatesOfGuest for details on each state.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 50)
    private StatesOfGuest state;

    @OneToMany(mappedBy = "carHolder")
    private List<FinancialAccount> paymentMethods;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookingList = new ArrayList<>();

    @OneToMany(mappedBy = "holder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactionHistory = new ArrayList<>();
}
