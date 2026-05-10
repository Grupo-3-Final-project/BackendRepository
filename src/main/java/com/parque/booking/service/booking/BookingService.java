package com.parque.booking.service.booking;

import com.parque.booking.dto.BookingCreateRequest;
import com.parque.booking.dto.BookingResponse;
import com.parque.booking.dto.BookingSummaryResponse;

import java.util.List;


public interface BookingService {

    BookingResponse create(BookingCreateRequest request);

    List<BookingSummaryResponse> getAll();

    BookingResponse getById(Long id);
}
