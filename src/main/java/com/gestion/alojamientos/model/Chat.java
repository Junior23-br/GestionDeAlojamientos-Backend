package com.gestion.alojamientos.model;

import java.util.ArrayList;
import java.util.List;

import com.gestion.alojamientos.model.base.SuperUser;
import com.gestion.alojamientos.model.users.Guest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id; // Unique identifier

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Message> messageList = new ArrayList<>();

    // Relación Muchos-a-Muchos con SuperUser (Tabla intermedia chat_members)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "chat_members", // Nombre tabla intermedia
        joinColumns = @JoinColumn(name = "chat_id"), // FK de Chat
        inverseJoinColumns = @JoinColumn(name = "user_id") // FK de SuperUser
    )
    @Builder.Default
    private List<Guest> membersList = new ArrayList<>();
}
