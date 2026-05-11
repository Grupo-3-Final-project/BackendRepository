package com.parque.dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String API_BASE = "/api";
    private static final String ADMIN_TOKEN = "demo-admin-token";

    @Test
    void testDashboardMetrics_AfterBooking() throws Exception {
        // Step 1: Create a booking (simulate)
        String bookingJson = """
                {
                    "userId": 1,
                    "hotelId": 1,
                    "checkInDate": "2026-05-15",
                    "checkOutDate": "2026-05-20",
                    "totalPrice": 500.00,
                    "numberOfGuests": 2
                }
                """;

        mockMvc.perform(post(API_BASE + "/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)
                .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isCreated());

        // Step 2: Query dashboard for updated metrics
        mockMvc.perform(get(API_BASE + "/dashboard/metrics")
                .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").exists())
                .andExpect(jsonPath("$.totalBookings").exists())
                .andExpect(jsonPath("$.occupancyRate").exists());
    }

    @Test
    void testDashboardRevenuCalculation() throws Exception {
        // Revenue should be calculated correctly after bookings
        mockMvc.perform(get(API_BASE + "/dashboard/revenue")
                .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").isNumber())
                .andExpect(jsonPath("$.period").exists());
    }

    @Test
    void testDashboardOccupancyRate() throws Exception {
        // Occupancy rate calculation
        mockMvc.perform(get(API_BASE + "/dashboard/occupancy")
                .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupancyRate").isNumber())
                .andExpect(jsonPath("$.totalRooms").isNumber())
                .andExpect(jsonPath("$.occupiedRooms").isNumber());
    }

    @Test
    void testDashboardUnauthorizedAccess() throws Exception {
        // Dashboard should require authentication
        mockMvc.perform(get(API_BASE + "/dashboard/metrics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDashboardForbiddenAccess() throws Exception {
        // Dashboard should require ADMIN role
        String userToken = "demo-user-token";
        mockMvc.perform(get(API_BASE + "/dashboard/metrics")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDashboardBookingStats() throws Exception {
        // Get booking statistics
        mockMvc.perform(get(API_BASE + "/dashboard/bookings/stats")
                .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBookings").exists())
                .andExpect(jsonPath("$.confirmedBookings").exists())
                .andExpect(jsonPath("$.cancelledBookings").exists())
                .andExpect(jsonPath("$.averageBookingValue").exists());
    }

    @Test
    void testDashboardHotelStats() throws Exception {
        // Get hotel statistics
        mockMvc.perform(get(API_BASE + "/dashboard/hotels/stats")
                .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHotels").isNumber())
                .andExpect(jsonPath("$.avgOccupancy").isNumber())
                .andExpect(jsonPath("$.avgPrice").isNumber());
    }
}
