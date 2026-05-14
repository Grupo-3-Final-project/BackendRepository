package com.parque.booking.service.notification;

import com.parque.booking.model.Booking;
import com.parque.entity.Ticket;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnExpression("'${spring.mail.host:}'.trim().length() > 0")
public class NotificationService {

    private final MailSender mailSender;
    private final QrCodeService qrCodeService;

    public NotificationService(MailSender mailSender, QrCodeService qrCodeService) {
        this.mailSender = mailSender;
        this.qrCodeService = qrCodeService;
    }

    public boolean sendBookingConfirmation(List<String> emails, Booking booking) {
        if (booking == null) {
            return false;
        }

        if (emails == null || emails.isEmpty()) {
            return false;
        }

        String subject = "Confirmacion de reserva - La Ultima Puerta";
        String body = buildBody(booking);
        Map<String, byte[]> inlineImages = buildInlineImages(booking);

        List<String> validEmails = emails.stream()
                .filter(email -> email != null && !email.isBlank())
                .toList();

        if (validEmails.isEmpty()) {
            return false;
        }

        return validEmails.stream()
                .allMatch(email -> mailSender.sendEmail(email, subject, body, inlineImages));
    }

    private Map<String, byte[]> buildInlineImages(Booking booking) {
        Map<String, byte[]> inlineImages = new LinkedHashMap<>();

        if (booking.getTickets() == null) {
            return inlineImages;
        }

        for (Ticket ticket : booking.getTickets()) {
            inlineImages.put(
                    entryQrId(ticket),
                    qrCodeService.generateQrCode(qrCodeService.buildEntryAccessUrl(ticket.getEntryToken()))
            );
            inlineImages.put(
                    mobileQrId(ticket),
                    qrCodeService.generateQrCode(qrCodeService.buildMobileAccessUrl(ticket.getMobileAccessToken()))
            );
        }

        return inlineImages;
    }

    private String buildBody(Booking booking) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body style=\"background:#050505;color:#f5f5f5;font-family:Arial,sans-serif;padding:24px;\">");
        builder.append("<div style=\"max-width:720px;margin:0 auto;border:1px solid rgba(255,255,255,0.08);border-radius:16px;background:#111;padding:24px;\">");
        builder.append("<h1 style=\"margin:0 0 16px 0;font-size:24px;\">Confirmacion de reserva</h1>");
        builder.append("<p style=\"margin:0 0 16px 0;color:#d6d3d1;\">Tu compra se ha registrado correctamente.</p>");
        builder.append("<div style=\"margin-bottom:24px;padding:16px;border:1px solid rgba(255,255,255,0.08);border-radius:12px;background:#181616;\">");
        builder.append("<p style=\"margin:0 0 8px 0;\"><strong>Reserva:</strong> #").append(booking.getId()).append("</p>");
        builder.append("<p style=\"margin:0 0 8px 0;\"><strong>Fecha de visita:</strong> ").append(booking.getVisitDate()).append("</p>");
        builder.append("<p style=\"margin:0 0 8px 0;\"><strong>Hotel:</strong> ")
                .append(booking.getHotel() == null ? "Sin hotel" : booking.getHotel().getName())
                .append("</p>");
        builder.append("<p style=\"margin:0 0 8px 0;\"><strong>Tipo de pension:</strong> ").append(booking.getBoardType()).append("</p>");
        builder.append("<p style=\"margin:0;\"><strong>Precio total:</strong> ").append(booking.getTotalPrice()).append(" EUR</p>");
        builder.append("</div>");

        if (booking.getTickets() != null) {
            for (Ticket ticket : booking.getTickets()) {
                builder.append("<div style=\"margin-bottom:16px;padding:16px;border:1px solid rgba(220,38,38,0.35);border-radius:12px;background:#181616;\">");
                builder.append("<h2 style=\"margin:0 0 12px 0;font-size:18px;color:#ffffff;\">").append(ticket.getHolderFullName()).append("</h2>");
                builder.append("<p style=\"margin:0 0 16px 0;color:#d6d3d1;\">Entrada personal para el parque y acceso a la web movil.</p>");
                builder.append("<table style=\"width:100%;border-collapse:collapse;\">");
                builder.append("<tr>");
                builder.append("<td style=\"width:50%;padding-right:8px;vertical-align:top;\">");
                builder.append("<p style=\"margin:0 0 8px 0;font-weight:bold;\">QR de entrada</p>");
                builder.append("<img alt=\"QR de entrada\" src=\"cid:").append(entryQrId(ticket)).append("\" style=\"display:block;width:180px;height:180px;background:#fff;border-radius:12px;padding:8px;\"/>");
                builder.append("<p style=\"margin:12px 0 0 0;color:#d6d3d1;word-break:break-word;\">")
                        .append(qrCodeService.buildEntryAccessUrl(ticket.getEntryToken()))
                        .append("</p>");
                builder.append("</td>");
                builder.append("<td style=\"width:50%;padding-left:8px;vertical-align:top;\">");
                builder.append("<p style=\"margin:0 0 8px 0;font-weight:bold;\">QR de acceso movil</p>");
                builder.append("<img alt=\"QR de acceso movil\" src=\"cid:").append(mobileQrId(ticket)).append("\" style=\"display:block;width:180px;height:180px;background:#fff;border-radius:12px;padding:8px;\"/>");
                builder.append("<p style=\"margin:12px 0 0 0;color:#d6d3d1;word-break:break-word;\">")
                        .append(qrCodeService.buildMobileAccessUrl(ticket.getMobileAccessToken()))
                        .append("</p>");
                builder.append("</td>");
                builder.append("</tr>");
                builder.append("</table>");
                builder.append("</div>");
            }
        }

        builder.append("</div></body></html>");
        return builder.toString();
    }

    private String entryQrId(Ticket ticket) {
        return "entry-" + ticket.getId();
    }

    private String mobileQrId(Ticket ticket) {
        return "mobile-" + ticket.getId();
    }
}
