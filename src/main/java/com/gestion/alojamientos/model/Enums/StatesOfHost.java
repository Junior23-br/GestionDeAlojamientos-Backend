package com.gestion.alojamientos.model.Enums;

public enum StatesOfHost {
    ACTIVE("El anfitrión está activo y puede ofrecer alojamientos"),
    INACTIVE("El anfitrión está inactivo y no puede recibir reservas"),
    SUSPENDED("El anfitrión ha sido suspendido temporalmente por incumplir políticas"),
    DELETED("La cuenta del anfitrión ha sido eliminada permanentemente"),
    PENDING("La cuenta del anfitrión está pendiente de revisión o aprobación"),
    APPROVED("La cuenta del anfitrión ha sido aprobada y está lista para operar"),
    REJECTED("La solicitud del anfitrión fue rechazada");

    private final String descripcion;

    StatesOfHost(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}