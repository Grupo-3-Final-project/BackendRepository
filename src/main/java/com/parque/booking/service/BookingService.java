package com.parque.booking.service;

import com.parque.booking.dto.BookingCreateRequest;
import com.parque.booking.dto.BookingResponse;
import com.parque.booking.dto.BookingSummaryResponse;
import com.parque.enums.PaymentStatus;

import java.util.List;

public interface BookingService {

    BookingResponse create(BookingCreateRequest request);

    List<BookingSummaryResponse> getAll();

    BookingResponse getById(Long id);

    PaymentStatus SetBookStatus(PaymentStatus Status);

    public boolean ChangeStatus(BookingResponse book, PaymentStatus status);
}
