package com.parque.user;

import com.parque.exception.ConflictException;
import com.parque.exception.ResourceNotFoundException;
import com.parque.user.dto.UserCreateRequest;
import com.parque.user.dto.UserResponse;
import com.parque.user.dto.UserUpdateRequest;
import com.parque.user.repository.UserRepository;
import com.parque.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void create_shouldCreateUser() {
        UserCreateRequest request = new UserCreateRequest(
                "David",
                "Navarro",
                "12345678A",
                "david@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        );

        UserResponse created = userService.create(request);

        assertThat(created.id()).isNotNull();
        assertThat(created.firstName()).isEqualTo("David");
        assertThat(created.dni()).isEqualTo("12345678A");
        assertThat(created.birthDate()).isEqualTo(LocalDate.parse("1990-04-15"));
    }

    @Test
    void create_shouldThrowConflict_whenEmailExists() {
        userService.create(new UserCreateRequest(
                "David",
                "Navarro",
                "12345678A",
                "david@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        ));

        assertThatThrownBy(() -> userService.create(new UserCreateRequest(
                "Ana",
                "Garcia",
                "87654321B",
                "david@example.com",
                "600000000",
                LocalDate.parse("1995-01-01")
        ))).isInstanceOf(ConflictException.class).hasMessage("Email already exists");
    }

    @Test
    void create_shouldThrowConflict_whenDniExists() {
        userService.create(new UserCreateRequest(
                "David",
                "Navarro",
                "12345678A",
                "david@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        ));

        assertThatThrownBy(() -> userService.create(new UserCreateRequest(
                "Ana",
                "Garcia",
                "12345678A",
                "ana@example.com",
                "600000000",
                LocalDate.parse("1995-01-01")
        ))).isInstanceOf(ConflictException.class).hasMessage("DNI already exists");
    }

    @Test
    void getOperations_shouldReturnStoredUsers() {
        UserResponse created = userService.create(new UserCreateRequest(
                "David",
                "Navarro",
                "12345678A",
                "david@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        ));

        assertThat(userService.getAll()).extracting(UserResponse::id).contains(created.id());
        assertThat(userService.getById(created.id()).email()).isEqualTo("david@example.com");
        assertThat(userService.getByUsername("david@example.com").dni()).isEqualTo("12345678A");
    }

    @Test
    void update_shouldPersistChanges() {
        UserResponse created = userService.create(new UserCreateRequest(
                "David",
                "Navarro",
                "12345678A",
                "david@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        ));

        UserResponse updated = userService.update(created.id(), new UserUpdateRequest(
                "Ana",
                "Garcia",
                "87654321B",
                "ana@example.com",
                "699999999",
                LocalDate.parse("1995-01-01")
        ));

        assertThat(updated.firstName()).isEqualTo("Ana");
        assertThat(updated.lastName()).isEqualTo("Garcia");
        assertThat(updated.dni()).isEqualTo("87654321B");
        assertThat(updated.email()).isEqualTo("ana@example.com");
    }

    @Test
    void update_shouldThrowConflict_whenEmailOrDniBelongsToAnotherUser() {
        UserResponse firstUser = userService.create(new UserCreateRequest(
                "David",
                "Navarro",
                "12345678A",
                "david@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        ));
        UserResponse secondUser = userService.create(new UserCreateRequest(
                "Ana",
                "Garcia",
                "87654321B",
                "ana@example.com",
                "600000000",
                LocalDate.parse("1995-01-01")
        ));

        assertThatThrownBy(() -> userService.update(firstUser.id(), new UserUpdateRequest(
                "David",
                "Navarro",
                "12345678A",
                "ana@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        ))).isInstanceOf(ConflictException.class).hasMessage("Email already exists");

        assertThatThrownBy(() -> userService.update(secondUser.id(), new UserUpdateRequest(
                "Ana",
                "Garcia",
                "12345678A",
                "ana@example.com",
                "600000000",
                LocalDate.parse("1995-01-01")
        ))).isInstanceOf(ConflictException.class).hasMessage("DNI already exists");
    }

    @Test
    void delete_shouldRemoveExistingUserAndRejectUnknownId() {
        UserResponse created = userService.create(new UserCreateRequest(
                "David",
                "Navarro",
                "12345678A",
                "david@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        ));

        userService.delete(created.id());

        assertThat(userRepository.existsById(created.id())).isFalse();
        assertThatThrownBy(() -> userService.delete(created.id()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void update_shouldThrowNotFound_whenUserDoesNotExist() {
        assertThatThrownBy(() -> userService.update(999L, new UserUpdateRequest(
                "David",
                "Navarro",
                "12345678A",
                "david@example.com",
                "600123123",
                LocalDate.parse("1990-04-15")
        ))).isInstanceOf(ResourceNotFoundException.class).hasMessage("User not found");
    }
}

