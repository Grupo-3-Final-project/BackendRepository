package com.parque.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingE2ETest {

    @Autowired
    private MockMvc mockMvc;

    private static final String API_BASE = "/api";
    private String authToken;

    @BeforeEach
    void setUp() {
        // Token demo para tests
        authToken = "demo-token-for-e2e-tests";
    }

    @Test
    void testCompleteBookingFlow_Success() throws Exception {
        // Step 1: Crear usuario
        String createUserJson = """
                {
                    "name": "Juan Perez",
                    "email": "juan@example.com",
                    "dni": "12345678A",
                    "age": 30,
                    "phone": "666123456"
                }
                """;

        mockMvc.perform(post(API_BASE + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        // Step 2: Listar hoteles disponibles
        mockMvc.perform(get(API_BASE + "/hotels")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Step 3: Listar atracciones
        mockMvc.perform(get(API_BASE + "/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Step 4: Crear booking (simulado)
        // Esto dependerá de que existan hoteles en BD
        String createBookingJson = """
                {
                    "userId": 1,
                    "hotelId": 1,
                    "checkInDate": "2026-05-15",
                    "checkOutDate": "2026-05-20",
                    "numberOfGuests": 2
                }
                """;

        mockMvc.perform(post(API_BASE + "/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBookingJson)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isCreated());
    }

    @Test
    void testBookingWithMinorWithoutGuardian_ShouldFail() throws Exception {
        // Test: Menor sin guardián debe fallar
        String createUserJson = """
                {
                    "name": "Pedro Lopez",
                    "email": "pedro@example.com",
                    "dni": "87654321B",
                    "age": 4,
                    "phone": "666654321"
                }
                """;

        mockMvc.perform(post(API_BASE + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testInvalidBookingDates_ShouldFail() throws Exception {
        // Test: Fechas inválidas (fin < inicio)
        String createBookingJson = """
                {
                    "userId": 1,
                    "hotelId": 1,
                    "checkInDate": "2026-05-20",
                    "checkOutDate": "2026-05-15",
                    "numberOfGuests": 2
                }
                """;

        mockMvc.perform(post(API_BASE + "/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBookingJson)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCorsHeadersPresent() throws Exception {
        // Test: Validar que CORS headers están presentes
        mockMvc.perform(get(API_BASE + "/hotels")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
