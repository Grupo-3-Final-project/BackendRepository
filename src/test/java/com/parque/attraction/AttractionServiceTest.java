package com.parque.attraction;

import com.parque.attraction.dto.AttractionCreateRequest;
import com.parque.attraction.dto.AttractionResponse;
import com.parque.attraction.dto.AttractionUpdateRequest;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.attraction.service.AttractionService;
import com.parque.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AttractionServiceTest {

    @Autowired
    private AttractionService attractionService;

    @Autowired
    private AttractionRepository attractionRepository;

    @BeforeEach
    void setUp() {
        attractionRepository.deleteAll();
    }

    @Test
    void create_shouldCalculateMaintenanceFrequencyDays() {
        AttractionCreateRequest request = new AttractionCreateRequest(
                "Dragon Coaster",
                "Montana rusa principal del parque.",
                "LARGE",
                "OPEN",
                32,
                32,
                "https://example.com/attraction.jpg"
        );

        AttractionResponse created = attractionService.create(request);

        assertThat(created.id()).isNotNull();
        assertThat(created.maintenanceFrequencyDays()).isEqualTo(7);
    }

    @Test
    void create_shouldCalculateMaintenanceFrequencyForSmallAndNormalizeValues() {
        AttractionResponse created = attractionService.create(new AttractionCreateRequest(
                "Pasaje del Olvido",
                "Recorrido inmersivo.",
                " small ",
                " maintenance ",
                18,
                8,
                "https://example.com/pasaje.jpg"
        ));

        assertThat(created.size()).isEqualTo("SMALL");
        assertThat(created.status()).isEqualTo("MAINTENANCE");
        assertThat(created.maintenanceFrequencyDays()).isEqualTo(30);
    }

    @Test
    void update_shouldKeepDefaultMaintenanceFrequencyForMediumAttractions() {
        AttractionResponse created = attractionService.create(new AttractionCreateRequest(
                "Laberinto",
                "Atraccion original.",
                "LARGE",
                "OPEN",
                20,
                20,
                "https://example.com/laberinto.jpg"
        ));

        AttractionResponse updated = attractionService.update(created.id(), new AttractionUpdateRequest(
                "Laberinto Oscuro",
                "Atraccion actualizada.",
                " medium ",
                " closed ",
                24,
                0,
                "https://example.com/laberinto-2.jpg"
        ));

        assertThat(updated.name()).isEqualTo("Laberinto Oscuro");
        assertThat(updated.size()).isEqualTo("MEDIUM");
        assertThat(updated.status()).isEqualTo("CLOSED");
        assertThat(updated.maintenanceFrequencyDays()).isEqualTo(14);
        assertThat(updated.availableSeats()).isZero();
    }

    @Test
    void getAllAndDelete_shouldReturnCreatedAttractionAndThenRemoveIt() {
        AttractionResponse created = attractionService.create(new AttractionCreateRequest(
                "Dragon Coaster",
                "Montana rusa principal del parque.",
                "LARGE",
                "OPEN",
                32,
                32,
                "https://example.com/attraction.jpg"
        ));

        assertThat(attractionService.getAll()).extracting(AttractionResponse::id).contains(created.id());
        assertThat(attractionService.getById(created.id()).name()).isEqualTo("Dragon Coaster");

        attractionService.delete(created.id());

        assertThat(attractionService.getAll()).isEmpty();
        assertThatThrownBy(() -> attractionService.getById(created.id()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Attraction not found");
    }

    @Test
    void update_shouldThrowNotFound_whenAttractionDoesNotExist() {
        AttractionUpdateRequest request = new AttractionUpdateRequest(
                "Dragon Coaster",
                "Montana rusa principal del parque.",
                "LARGE",
                "MAINTENANCE",
                32,
                0,
                "https://example.com/attraction.jpg"
        );

        assertThatThrownBy(() -> attractionService.update(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Attraction not found");
    }
}

