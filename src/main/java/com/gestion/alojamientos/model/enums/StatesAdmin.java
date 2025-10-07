package com.gestion.alojamientos.model.enums;

public enum StatesAdmin {
    ACTIVE ("Admin activo"),
    INACTIVE("Admin inactivo"),
    DELETED ("Admin eliminado");

    private String descripcion;

    StatesAdmin(String s) {
        this.descripcion = s;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
