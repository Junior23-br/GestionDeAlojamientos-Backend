package com.gestion.alojamientos.model.Enums;

public enum StatesOfGuest {
    ACTIVE("El huésped está activo y puede usar la plataforma"),
    DELETED("La cuenta del huésped ha sido eliminada permanentemente"),
    SUSPENDED("El huésped ha sido suspendido temporalmente por incumplir reglas"),
    INACTIVE("La cuenta del huésped está inactiva por falta de uso o decisión del usuario");

    private final String description;


    StatesOfGuest(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }

}
