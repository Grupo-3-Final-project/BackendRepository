package com.parque.booking.service.notification;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.parque.exception.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

@Service
public class QrCodeService {

    private static final int QR_SIZE = 220;
    private static final String DEFAULT_BASE_URL = "http://localhost:5173";

    private final String mobileBaseUrl;
    private final String entryBaseUrl;

    public QrCodeService(
            @Value("${app.urls.base-mobile:http://localhost:5173}") String baseMobileUrl,
            @Value("${app.urls.base-entry:http://localhost:5173}") String baseEntryUrl
    ) {
        this.mobileBaseUrl = buildBaseUrl(baseMobileUrl, DEFAULT_BASE_URL, "mobile");
        this.entryBaseUrl = buildBaseUrl(baseEntryUrl, DEFAULT_BASE_URL, "entry");
    }

    public byte[] generateQrCode(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException exception) {
            throw new InternalServerErrorException("QR generation failed");
        }
    }

    public String buildEntryAccessUrl(String entryToken) {
        return entryBaseUrl + "/" + entryToken;
    }

    public String buildMobileAccessUrl(String mobileAccessToken) {
        return mobileBaseUrl + "/" + mobileAccessToken;
    }

    private String buildBaseUrl(String value, String fallbackValue, String path) {
        String normalized = normalizeBaseUrl(value, fallbackValue);
        if (normalized.endsWith("/" + path)) {
            return normalized;
        }
        return normalized + "/" + path;
    }

    private String normalizeBaseUrl(String value, String fallbackValue) {
        if (value == null || value.isBlank()) {
            return fallbackValue;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
