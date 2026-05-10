package com.parque.attraction.controller;

import com.parque.attraction.dto.AttractionCreateRequest;
import com.parque.attraction.dto.AttractionResponse;
import com.parque.attraction.dto.AttractionUpdateRequest;
import com.parque.attraction.service.AttractionService;
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
@RequestMapping("/api/v1/attractions")
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    @GetMapping
    public List<AttractionResponse> getAll() {
        return attractionService.getAll();
    }

    @GetMapping("/{id}")
    public AttractionResponse getById(@PathVariable Long id) {
        return attractionService.getById(id);
    }

    @PostMapping
    public ResponseEntity<AttractionResponse> create(@Valid @RequestBody AttractionCreateRequest request) {
        AttractionResponse created = attractionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public AttractionResponse update(@PathVariable Long id, @Valid @RequestBody AttractionUpdateRequest request) {
        return attractionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attractionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

