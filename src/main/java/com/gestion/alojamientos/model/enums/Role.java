package com.gestion.alojamientos.model.enums;

import lombok.Getter;

@Getter
public enum Role {
    GUEST("Usuario estándar que realiza reservas"),
    HOST("Anfitrión que gestiona alojamientos");
    private final String description;
    Role(String description) {
        this.description = description;
    }
}