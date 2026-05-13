package com.parque.offer.controller;

import com.parque.offer.dto.OfferCreateRequest;
import com.parque.offer.dto.OfferResponse;
import com.parque.offer.dto.OfferUpdateRequest;
import com.parque.offer.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public List<OfferResponse> getAll() {
        return offerService.getAll();
    }

    @GetMapping("/{id}")
    public OfferResponse getById(@PathVariable Long id) {
        return offerService.getById(id);
    }

    @PostMapping
    public ResponseEntity<OfferResponse> create(@Valid @RequestBody OfferCreateRequest request) {
        OfferResponse created = offerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public OfferResponse update(@PathVariable Long id, @Valid @RequestBody OfferUpdateRequest request) {
        return offerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        offerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

