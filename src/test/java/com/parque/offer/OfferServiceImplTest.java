package com.parque.offer;

import com.parque.exception.ResourceNotFoundException;
import com.parque.hotel.model.Hotel;
import com.parque.hotel.repository.HotelRepository;
import com.parque.offer.dto.OfferCreateRequest;
import com.parque.offer.dto.OfferUpdateRequest;
import com.parque.offer.model.Offer;
import com.parque.offer.repository.OfferRepository;
import com.parque.offer.service.OfferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private HotelRepository hotelRepository;

    private OfferServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OfferServiceImpl(offerRepository, hotelRepository);
    }

    @Test
    void getAll_shouldMapResponses() {
        Hotel hotel = buildHotel();
        Offer offer = buildOffer(7L, hotel);
        when(offerRepository.findAll()).thenReturn(List.of(offer));

        var response = service.getAll();

        assertThat(response).singleElement().satisfies(mapped -> {
            assertThat(mapped.id()).isEqualTo(7L);
            assertThat(mapped.title()).isEqualTo("Pack familiar");
            assertThat(mapped.hotelId()).isEqualTo(3L);
            assertThat(mapped.hotelName()).isEqualTo("Hotel Eclipse");
            assertThat(mapped.boardType()).isEqualTo("FULL_BOARD");
        });
    }

    @Test
    void create_shouldNormalizeBoardTypeAndPersistOffer() {
        Hotel hotel = buildHotel();
        when(hotelRepository.findById(3L)).thenReturn(Optional.of(hotel));
        when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> {
            Offer saved = invocation.getArgument(0);
            saved.setId(9L);
            return saved;
        });

        var response = service.create(new OfferCreateRequest(
                "Oferta premium",
                "Hotel y entradas",
                3L,
                " full_board ",
                4,
                new BigDecimal("399.99"),
                "https://example.com/offer.jpg"
        ));

        ArgumentCaptor<Offer> captor = ArgumentCaptor.forClass(Offer.class);
        verify(offerRepository).save(captor.capture());
        Offer saved = captor.getValue();

        assertThat(saved.getBoardType()).isEqualTo("FULL_BOARD");
        assertThat(saved.getHotel()).isSameAs(hotel);
        assertThat(response.id()).isEqualTo(9L);
        assertThat(response.boardType()).isEqualTo("FULL_BOARD");
    }

    @Test
    void create_shouldThrowWhenHotelDoesNotExist() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(new OfferCreateRequest(
                "Oferta premium",
                "Hotel y entradas",
                99L,
                "FULL_BOARD",
                4,
                new BigDecimal("399.99"),
                "https://example.com/offer.jpg"
        )))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Hotel not found");
    }

    @Test
    void update_shouldThrowWhenOfferDoesNotExist() {
        when(offerRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(55L, new OfferUpdateRequest(
                "Oferta premium",
                "Hotel y entradas",
                3L,
                "FULL_BOARD",
                4,
                new BigDecimal("399.99"),
                "https://example.com/offer.jpg"
        )))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Offer not found");
    }

    @Test
    void update_shouldPersistModifiedOffer() {
        Hotel hotel = buildHotel();
        Offer offer = buildOffer(5L, hotel);
        when(offerRepository.findById(5L)).thenReturn(Optional.of(offer));
        when(hotelRepository.findById(3L)).thenReturn(Optional.of(hotel));
        when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.update(5L, new OfferUpdateRequest(
                "Oferta noche",
                "Descripcion actualizada",
                3L,
                " half_board ",
                2,
                new BigDecimal("149.99"),
                "https://example.com/offer-night.jpg"
        ));

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.title()).isEqualTo("Oferta noche");
        assertThat(response.boardType()).isEqualTo("HALF_BOARD");
        assertThat(response.includedTickets()).isEqualTo(2);
        assertThat(response.totalPrice()).isEqualByComparingTo("149.99");
    }

    @Test
    void delete_shouldThrowWhenOfferDoesNotExist() {
        when(offerRepository.existsById(44L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(44L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Offer not found");
    }

    @Test
    void delete_shouldRemoveOfferWhenItExists() {
        when(offerRepository.existsById(44L)).thenReturn(true);

        service.delete(44L);

        verify(offerRepository).deleteById(44L);
    }

    private Hotel buildHotel() {
        return Hotel.builder()
                .id(3L)
                .name("Hotel Eclipse")
                .description("Hotel demo")
                .totalRooms(80)
                .availableRooms(60)
                .totalPlaces(160)
                .availablePlaces(120)
                .halfBoardPrice(new BigDecimal("80.00"))
                .fullBoardPrice(new BigDecimal("120.00"))
                .imageUrl("https://example.com/hotel.jpg")
                .build();
    }

    private Offer buildOffer(Long id, Hotel hotel) {
        return Offer.builder()
                .id(id)
                .title("Pack familiar")
                .description("Oferta demo")
                .hotel(hotel)
                .boardType("FULL_BOARD")
                .includedTickets(4)
                .totalPrice(new BigDecimal("399.99"))
                .imageUrl("https://example.com/offer.jpg")
                .build();
    }
}
