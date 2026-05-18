package com.parque.auth;

import com.parque.auth.model.InternalRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InternalRoleTest {

    @Test
    void roles_shouldExposeDescriptionsAndGrantedRoleNames() {
        assertThat(InternalRole.ADMIN.getDescription()).isEqualTo("Administrador del sistema");
        assertThat(InternalRole.MANAGER.getDescription()).isEqualTo("Gerente del parque");
        assertThat(InternalRole.EMPLOYEE.getDescription()).isEqualTo("Empleado del parque");
        assertThat(InternalRole.USER.getDescription()).isEqualTo("Usuario registrado");

        assertThat(InternalRole.ADMIN.getRoleName()).isEqualTo("ROLE_ADMIN");
        assertThat(InternalRole.MANAGER.getRoleName()).isEqualTo("ROLE_MANAGER");
        assertThat(InternalRole.EMPLOYEE.getRoleName()).isEqualTo("ROLE_EMPLOYEE");
        assertThat(InternalRole.USER.getRoleName()).isEqualTo("ROLE_USER");
    }
}
