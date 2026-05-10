package com.parque.auth.model;

public enum InternalRole {
    ADMIN("Administrador del sistema"),
    MANAGER("Gerente del parque"),
    EMPLOYEE("Empleado del parque"),
    USER("Usuario registrado");

    private final String description;

    InternalRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
