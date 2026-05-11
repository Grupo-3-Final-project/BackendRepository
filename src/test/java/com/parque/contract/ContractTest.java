package com.parque.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContractTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String API_BASE = "/api";

    // Test Suite: Error Response Format
    @Test
    void testErrorResponseStructure_BadRequest() throws Exception {
        String invalidJson = """
                {
                    "name": "",
                    "age": -5
                }
                """;

        mockMvc.perform(post(API_BASE + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testErrorResponseStructure_NotFound() throws Exception {
        mockMvc.perform(get(API_BASE + "/users/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    // Test Suite: HTTP Status Codes
    @Test
    void testHttpStatusCode_CreateSuccess() throws Exception {
        String validUserJson = """
                {
                    "name": "Test User",
                    "email": "test@example.com",
                    "dni": "12345678A",
                    "age": 25
                }
                """;

        mockMvc.perform(post(API_BASE + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUserJson))
                .andExpect(status().isCreated());
    }

    @Test
    void testHttpStatusCode_GetSuccess() throws Exception {
        mockMvc.perform(get(API_BASE + "/hotels"))
                .andExpect(status().isOk());
    }

    @Test
    void testHttpStatusCode_Unauthorized() throws Exception {
        mockMvc.perform(get(API_BASE + "/admin/users")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    // Test Suite: Response Content-Type
    @Test
    void testResponseContentType_JSON() throws Exception {
        mockMvc.perform(get(API_BASE + "/attractions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Suite: Required Fields
    @Test
    void testUserResponse_RequiredFields() throws Exception {
        String validUserJson = """
                {
                    "name": "Test User",
                    "email": "test@example.com",
                    "dni": "12345678A",
                    "age": 25
                }
                """;

        mockMvc.perform(post(API_BASE + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.dni").exists());
    }

    // Test Suite: Date Format (ISO 8601)
    @Test
    void testDateFormat_ISO8601() throws Exception {
        // Booking with ISO 8601 dates
        String bookingJson = """
                {
                    "userId": 1,
                    "hotelId": 1,
                    "checkInDate": "2026-05-15",
                    "checkOutDate": "2026-05-20"
                }
                """;

        mockMvc.perform(post(API_BASE + "/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checkInDate").exists());
    }

    // Test Suite: Numeric Format (Prices as decimals)
    @Test
    void testPriceFormat_Decimal() throws Exception {
        // Offer with decimal price
        mockMvc.perform(get(API_BASE + "/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].discount").exists());
    }

    // Test Suite: CORS Validation
    @Test
    void testCorsHeaders_PreflightRequest() throws Exception {
        mockMvc.perform(options(API_BASE + "/users")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    void testCorsHeaders_SimpleRequest() throws Exception {
        mockMvc.perform(get(API_BASE + "/hotels")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    // Test Suite: Error Code Consistency
    @Test
    void testErrorCode_Validation() throws Exception {
        String invalidJson = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(post(API_BASE + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void testErrorCode_Conflict() throws Exception {
        // Test duplicate email (conflict)
        String userJson = """
                {
                    "name": "Test User",
                    "email": "duplicate@example.com",
                    "dni": "12345678A",
                    "age": 25
                }
                """;

        mockMvc.perform(post(API_BASE + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

        // Same email again
        mockMvc.perform(post(API_BASE + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
