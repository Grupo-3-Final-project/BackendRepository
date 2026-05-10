package com.parque.shift.service;

import com.parque.shift.dto.ShiftGenerateRequest;
import com.parque.shift.dto.ShiftGenerateResponse;
import com.parque.shift.dto.ShiftResponse;

import java.util.List;

public interface ShiftService {
    List<ShiftResponse> getAll();

    ShiftGenerateResponse generate(ShiftGenerateRequest request);
}

