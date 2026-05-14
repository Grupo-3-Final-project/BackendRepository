package com.parque.booking;

import com.parque.booking.service.notification.QrCodeService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QrCodeServiceTest {

    @Test
    void buildEntryAccessUrl_shouldDeriveEntryRouteFromMobileBaseUrl() {
        QrCodeService qrCodeService = new QrCodeService("http://localhost:5173/mobile/", "");

        assertThat(qrCodeService.buildEntryAccessUrl("entry-token")).isEqualTo("http://localhost:5173/entry/entry-token");
        assertThat(qrCodeService.buildMobileAccessUrl("mobile-token")).isEqualTo("http://localhost:5173/mobile/mobile-token");
    }

    @Test
    void buildEntryAccessUrl_shouldUseExplicitEntryBaseUrlWhenConfigured() {
        QrCodeService qrCodeService = new QrCodeService(
                "http://localhost:5173/mobile",
                "https://tickets.parque-atracciones.com/entry/"
        );

        assertThat(qrCodeService.buildEntryAccessUrl("entry-token"))
                .isEqualTo("https://tickets.parque-atracciones.com/entry/entry-token");
    }
}
