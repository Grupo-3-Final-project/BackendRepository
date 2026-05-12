package com.parque.maintenance.controller;

import com.parque.maintenance.dto.MaintenanceGenerateRequest;
import com.parque.maintenance.dto.MaintenanceGenerateResponse;
import com.parque.maintenance.dto.MaintenanceResponse;
import com.parque.maintenance.service.MaintenanceService;
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
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public List<MaintenanceResponse> getAll() {
        return maintenanceService.getAll();
    }

    @PostMapping("/generate")
    public ResponseEntity<MaintenanceGenerateResponse> generate(@Valid @RequestBody MaintenanceGenerateRequest request) {
        MaintenanceGenerateResponse generated = maintenanceService.generate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(generated);
    }
}

