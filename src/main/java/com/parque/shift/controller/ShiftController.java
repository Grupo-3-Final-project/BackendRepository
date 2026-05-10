package com.parque.shift.controller;

import com.parque.shift.dto.ShiftGenerateRequest;
import com.parque.shift.dto.ShiftGenerateResponse;
import com.parque.shift.dto.ShiftResponse;
import com.parque.shift.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping
    public List<ShiftResponse> getAll() {
        return shiftService.getAll();
    }

    @PostMapping("/generate")
    public ResponseEntity<ShiftGenerateResponse> generate(@Valid @RequestBody ShiftGenerateRequest request) {
        ShiftGenerateResponse generated = shiftService.generate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(generated);
    }
}

