package com.parque.offer.service;

import com.parque.exception.ResourceNotFoundException;
import com.parque.hotel.model.Hotel;
import com.parque.hotel.repository.HotelRepository;
import com.parque.offer.dto.OfferCreateRequest;
import com.parque.offer.dto.OfferResponse;
import com.parque.offer.dto.OfferUpdateRequest;
import com.parque.offer.model.Offer;
import com.parque.offer.repository.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        Offer offer = new Offer();
        applyRequest(offer, request.hotelId(), request.title(), request.description(), request.boardType(), request.includedTickets(), request.totalPrice(), request.imageUrl());
        return toResponse(offerRepository.save(offer));
    }

    @Override
    public OfferResponse update(Long id, OfferUpdateRequest request) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        applyRequest(offer, request.hotelId(), request.title(), request.description(), request.boardType(), request.includedTickets(), request.totalPrice(), request.imageUrl());
        return toResponse(offerRepository.save(offer));
    }

    @Override
    public void delete(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Offer not found");
        }
        offerRepository.deleteById(id);
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

    private void applyRequest(
            Offer offer,
            Long hotelId,
            String title,
            String description,
            String boardType,
            Integer includedTickets,
            BigDecimal totalPrice,
            String imageUrl
    ) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        offer.setTitle(title);
        offer.setDescription(description);
        offer.setHotel(hotel);
        offer.setBoardType(boardType.trim().toUpperCase());
        offer.setIncludedTickets(includedTickets);
        offer.setTotalPrice(totalPrice);
        offer.setImageUrl(imageUrl);
    }
}

