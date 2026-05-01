package com.parque.offer.service;

import com.parque.entity.Hotel;
import com.parque.entity.Offer;
import com.parque.exception.ResourceNotFoundException;
import com.parque.hotel.repository.HotelRepository;
import com.parque.offer.dto.OfferCreateRequest;
import com.parque.offer.dto.OfferResponse;
import com.parque.offer.repository.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final HotelRepository hotelRepository;

    public OfferServiceImpl(OfferRepository offerRepository, HotelRepository hotelRepository) {
        this.offerRepository = offerRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferResponse> getAll() {
        return offerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OfferResponse getById(Long id) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
        return toResponse(offer);
    }

    @Override
    public OfferResponse create(OfferCreateRequest request) {
        Hotel hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Offer offer = Offer.builder()
                .title(request.title())
                .description(request.description())
                .hotel(hotel)
                .boardType(request.boardType())
                .includedTickets(request.includedTickets())
                .totalPrice(request.totalPrice())
                .imageUrl(request.imageUrl())
                .build();

        return toResponse(offerRepository.save(offer));
    }

    private OfferResponse toResponse(Offer offer) {
        return new OfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getHotel().getId(),
                offer.getHotel().getName(),
                offer.getBoardType(),
                offer.getIncludedTickets(),
                offer.getTotalPrice(),
                offer.getImageUrl()
        );
    }
}

