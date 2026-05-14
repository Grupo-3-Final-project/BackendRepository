package com.parque.booking.service.ticket;

import com.parque.booking.dto.MobileAccessResponse;
import com.parque.booking.dto.TicketEntryValidationResponse;

public interface TicketAccessService {

    MobileAccessResponse getMobileAccess(String mobileAccessToken);

    TicketEntryValidationResponse validateEntry(String entryToken);
}
