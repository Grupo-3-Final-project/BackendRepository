package com.parque.booking;

import com.parque.booking.model.Booking;
import com.parque.booking.service.notification.MailSender;
import com.parque.booking.service.notification.NotificationService;
import com.parque.booking.service.notification.QrCodeService;
import com.parque.entity.Ticket;
import com.parque.hotel.model.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private MailSender mailSender;

    @Mock
    private QrCodeService qrCodeService;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(mailSender, qrCodeService);
    }

    @Test
    void shouldReturnFalseWhenBookingOrEmailsAreMissing() {
        assertThat(notificationService.sendBookingConfirmation(null, sampleBooking())).isFalse();
        assertThat(notificationService.sendBookingConfirmation(List.of(), sampleBooking())).isFalse();
        assertThat(notificationService.sendBookingConfirmation(List.of("ana@example.com"), null)).isFalse();
        assertThat(notificationService.sendBookingConfirmation(Arrays.asList(" ", null), sampleBooking())).isFalse();
    }

    @Test
    void shouldSendHtmlMailWithEntryAndMobileQrForEachTicket() {
        Booking booking = sampleBooking();

        when(qrCodeService.buildEntryAccessUrl("entry-1")).thenReturn("https://entry/entry-1");
        when(qrCodeService.buildMobileAccessUrl("mobile-1")).thenReturn("https://mobile/mobile-1");
        when(qrCodeService.buildEntryAccessUrl("entry-2")).thenReturn("https://entry/entry-2");
        when(qrCodeService.buildMobileAccessUrl("mobile-2")).thenReturn("https://mobile/mobile-2");
        when(qrCodeService.generateQrCode("https://entry/entry-1")).thenReturn(new byte[]{1});
        when(qrCodeService.generateQrCode("https://mobile/mobile-1")).thenReturn(new byte[]{2});
        when(qrCodeService.generateQrCode("https://entry/entry-2")).thenReturn(new byte[]{3});
        when(qrCodeService.generateQrCode("https://mobile/mobile-2")).thenReturn(new byte[]{4});
        when(mailSender.sendEmail(eq("ana@example.com"), eq("Confirmacion de reserva - La Ultima Puerta"), org.mockito.ArgumentMatchers.contains("Confirmacion de reserva"), anyMap()))
                .thenReturn(true);
        when(mailSender.sendEmail(eq("luis@example.com"), eq("Confirmacion de reserva - La Ultima Puerta"), org.mockito.ArgumentMatchers.contains("Confirmacion de reserva"), anyMap()))
                .thenReturn(true);

        boolean sent = notificationService.sendBookingConfirmation(
                Arrays.asList("ana@example.com", " ", null, "luis@example.com"),
                booking
        );

        assertThat(sent).isTrue();

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, byte[]>> imagesCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mailSender).sendEmail(eq("ana@example.com"), eq("Confirmacion de reserva - La Ultima Puerta"), bodyCaptor.capture(), imagesCaptor.capture());

        assertThat(bodyCaptor.getValue())
                .contains("Reserva:</strong> #25")
                .contains("Hotel Umbral Nocturno")
                .contains("Ana Garcia")
                .contains("Pedro Garcia")
                .contains("https://entry/entry-1")
                .contains("https://mobile/mobile-2");

        assertThat(imagesCaptor.getValue())
                .containsKeys("entry-1", "mobile-1", "entry-2", "mobile-2")
                .containsEntry("entry-1", new byte[]{1})
                .containsEntry("mobile-2", new byte[]{4});
    }

    private Booking sampleBooking() {
        Ticket firstTicket = new Ticket();
        firstTicket.setId(1L);
        firstTicket.setHolderFullName("Ana Garcia");
        firstTicket.setEntryToken("entry-1");
        firstTicket.setMobileAccessToken("mobile-1");

        Ticket secondTicket = new Ticket();
        secondTicket.setId(2L);
        secondTicket.setHolderFullName("Pedro Garcia");
        secondTicket.setEntryToken("entry-2");
        secondTicket.setMobileAccessToken("mobile-2");

        Hotel hotel = Hotel.builder()
                .name("Hotel Umbral Nocturno")
                .build();

        Booking booking = new Booking();
        booking.setId(25L);
        booking.setVisitDate(LocalDate.parse("2026-05-22"));
        booking.setBoardType("FULL_BOARD");
        booking.setTotalPrice(new BigDecimal("399.99"));
        booking.setHotel(hotel);
        booking.setTickets(List.of(firstTicket, secondTicket));
        return booking;
    }
}
