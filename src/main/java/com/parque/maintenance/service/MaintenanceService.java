package com.parque.maintenance.service;

import com.parque.maintenance.dto.MaintenanceGenerateRequest;
import com.parque.maintenance.dto.MaintenanceGenerateResponse;
import com.parque.maintenance.dto.MaintenanceResponse;

import java.util.List;

public interface MaintenanceService {
    List<MaintenanceResponse> getAll();

    MaintenanceGenerateResponse generate(MaintenanceGenerateRequest request);
}

