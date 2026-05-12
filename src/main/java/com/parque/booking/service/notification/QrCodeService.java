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

    private final String mobileBaseUrl;

    public QrCodeService(@Value("${app.mobile-base-url:http://localhost:5173/mobile}") String mobileBaseUrl) {
        this.mobileBaseUrl = normalizeBaseUrl(mobileBaseUrl);
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

    public String buildEntryQrContent(String entryToken) {
        return "ENTRY:" + entryToken;
    }

    public String buildMobileAccessUrl(String mobileAccessToken) {
        return mobileBaseUrl + "/" + mobileAccessToken;
    }

    private String normalizeBaseUrl(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:5173/mobile";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
