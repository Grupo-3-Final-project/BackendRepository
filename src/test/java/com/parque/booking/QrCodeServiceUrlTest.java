package com.parque.booking;

import com.parque.booking.service.notification.QrCodeService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QrCodeServiceUrlTest {

    @Test
    void buildEntryAccessUrl_shouldAppendEntryPathWhenMobileBaseDoesNotEndWithMobile() {
        QrCodeService qrCodeService = new QrCodeService("https://tickets.parque.com/access", "");

        assertThat(qrCodeService.buildMobileAccessUrl("mobile-token"))
                .isEqualTo("https://tickets.parque.com/access/mobile-token");
        assertThat(qrCodeService.buildEntryAccessUrl("entry-token"))
                .isEqualTo("https://tickets.parque.com/access/entry/entry-token");
    }
}
