package com.parque.booking;

import com.parque.booking.service.notification.QrCodeService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QrCodeServiceTest {

    @Test
    void generateQrCode_shouldCreatePngBytes() {
        QrCodeService qrCodeService = new QrCodeService("http://localhost:5173/mobile", "");

        byte[] qrCode = qrCodeService.generateQrCode("https://parque.test/mobile/token-1");

        assertThat(qrCode).isNotEmpty();
        assertThat(qrCode[0]).isEqualTo((byte) 0x89);
        assertThat(qrCode[1]).isEqualTo((byte) 0x50);
        assertThat(qrCode[2]).isEqualTo((byte) 0x4E);
        assertThat(qrCode[3]).isEqualTo((byte) 0x47);
    }

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

    @Test
    void buildEntryAccessUrl_shouldFallbackToDefaultBaseUrlWhenMobileBaseIsBlank() {
        QrCodeService qrCodeService = new QrCodeService("   ", null);

        assertThat(qrCodeService.buildMobileAccessUrl("mobile-token")).isEqualTo("http://localhost:5173/mobile/mobile-token");
        assertThat(qrCodeService.buildEntryAccessUrl("entry-token")).isEqualTo("http://localhost:5173/entry/entry-token");
    }
}
