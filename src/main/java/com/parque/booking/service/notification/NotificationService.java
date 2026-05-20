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
        String textBody = buildTextBody(booking);
        String htmlBody = buildHtmlBody(booking);
        Map<String, byte[]> inlineImages = buildInlineImages(booking);

        List<String> validEmails = emails.stream()
                .filter(email -> email != null && !email.isBlank())
                .toList();

        if (validEmails.isEmpty()) {
            return false;
        }

        return validEmails.stream()
                .allMatch(email -> mailSender.sendEmail(email, subject, textBody, htmlBody, inlineImages));
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

    private String buildTextBody(Booking booking) {
        StringBuilder builder = new StringBuilder();
        builder.append("Confirmacion de reserva").append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append("Tu compra se ha registrado correctamente.").append(System.lineSeparator());
        builder.append("Reserva: #").append(booking.getId()).append(System.lineSeparator());
        builder.append("Fecha de visita: ").append(booking.getVisitDate()).append(System.lineSeparator());
        builder.append("Hotel: ").append(booking.getHotel() == null ? "Sin hotel" : booking.getHotel().getName()).append(System.lineSeparator());
        builder.append("Tipo de pension: ").append(booking.getBoardType()).append(System.lineSeparator());
        builder.append("Precio total: ").append(booking.getTotalPrice()).append(" EUR").append(System.lineSeparator());

        if (booking.getTickets() != null && !booking.getTickets().isEmpty()) {
            builder.append(System.lineSeparator());
            builder.append("Entradas").append(System.lineSeparator());
            for (Ticket ticket : booking.getTickets()) {
                builder.append(System.lineSeparator());
                builder.append(ticket.getHolderFullName()).append(System.lineSeparator());
                builder.append("QR de entrada: ").append(qrCodeService.buildEntryAccessUrl(ticket.getEntryToken())).append(System.lineSeparator());
                builder.append("Acceso movil: ").append(qrCodeService.buildMobileAccessUrl(ticket.getMobileAccessToken())).append(System.lineSeparator());
            }
        }

        return builder.toString();
    }

    private String buildHtmlBody(Booking booking) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body style=\"margin:0;padding:0;background:#f6f6f6;color:#222222;font-family:Arial,Helvetica,sans-serif;\">");
        builder.append("<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"background:#f6f6f6;width:100%;border-collapse:collapse;\">");
        builder.append("<tr><td align=\"center\" style=\"padding:24px 12px;\">");
        builder.append("<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"max-width:680px;width:100%;background:#ffffff;border:1px solid #dddddd;border-collapse:collapse;\">");
        builder.append("<tr><td style=\"padding:24px 24px 12px 24px;\">");
        builder.append("<h1 style=\"margin:0 0 12px 0;font-size:22px;line-height:28px;color:#222222;font-weight:bold;\">Confirmacion de reserva</h1>");
        builder.append("<p style=\"margin:0 0 18px 0;font-size:15px;line-height:22px;color:#444444;\">Tu compra se ha registrado correctamente.</p>");
        builder.append("</td></tr>");
        builder.append("<tr><td style=\"padding:0 24px 20px 24px;\">");
        builder.append("<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #e5e5e5;border-collapse:collapse;background:#fafafa;\">");
        builder.append("<tr><td style=\"padding:14px 16px;font-size:14px;line-height:21px;color:#333333;\">");
        builder.append("<p style=\"margin:0 0 8px 0;\"><strong>Reserva:</strong> #").append(booking.getId()).append("</p>");
        builder.append("<p style=\"margin:0 0 8px 0;\"><strong>Fecha de visita:</strong> ").append(booking.getVisitDate()).append("</p>");
        builder.append("<p style=\"margin:0 0 8px 0;\"><strong>Hotel:</strong> ")
                .append(booking.getHotel() == null ? "Sin hotel" : booking.getHotel().getName())
                .append("</p>");
        builder.append("<p style=\"margin:0 0 8px 0;\"><strong>Tipo de pension:</strong> ").append(booking.getBoardType()).append("</p>");
        builder.append("<p style=\"margin:0;\"><strong>Precio total:</strong> ").append(booking.getTotalPrice()).append(" EUR</p>");
        builder.append("</td></tr></table>");
        builder.append("</td></tr>");

        if (booking.getTickets() != null) {
            for (Ticket ticket : booking.getTickets()) {
                builder.append("<tr><td style=\"padding:0 24px 20px 24px;\">");
                builder.append("<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #e5e5e5;border-collapse:collapse;background:#ffffff;\">");
                builder.append("<tr><td style=\"padding:16px 16px 8px 16px;\">");
                builder.append("<h2 style=\"margin:0 0 8px 0;font-size:17px;line-height:24px;color:#222222;font-weight:bold;\">").append(ticket.getHolderFullName()).append("</h2>");
                builder.append("<p style=\"margin:0;font-size:14px;line-height:21px;color:#555555;\">Entrada personal para el parque y acceso a la web movil.</p>");
                builder.append("</td></tr>");
                builder.append("<tr><td style=\"padding:8px 16px 16px 16px;\">");
                builder.append("<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:100%;border-collapse:collapse;\">");
                builder.append("<tr>");
                builder.append("<td width=\"50%\" style=\"padding:0 8px 0 0;vertical-align:top;font-size:14px;line-height:21px;color:#333333;\">");
                builder.append("<p style=\"margin:0 0 8px 0;font-weight:bold;color:#222222;\">QR de entrada</p>");
                builder.append("<img alt=\"Codigo QR de entrada para ").append(ticket.getHolderFullName()).append("\" src=\"cid:").append(entryQrId(ticket)).append("\" width=\"180\" height=\"180\" style=\"display:block;width:180px;height:180px;border:1px solid #dddddd;background:#ffffff;padding:8px;\"/>");
                builder.append("<p style=\"margin:10px 0 0 0;color:#555555;font-size:12px;line-height:18px;word-break:break-word;\">")
                        .append(qrCodeService.buildEntryAccessUrl(ticket.getEntryToken()))
                        .append("</p>");
                builder.append("</td>");
                builder.append("<td width=\"50%\" style=\"padding:0 0 0 8px;vertical-align:top;font-size:14px;line-height:21px;color:#333333;\">");
                builder.append("<p style=\"margin:0 0 8px 0;font-weight:bold;color:#222222;\">QR de acceso movil</p>");
                builder.append("<img alt=\"Codigo QR de acceso movil para ").append(ticket.getHolderFullName()).append("\" src=\"cid:").append(mobileQrId(ticket)).append("\" width=\"180\" height=\"180\" style=\"display:block;width:180px;height:180px;border:1px solid #dddddd;background:#ffffff;padding:8px;\"/>");
                builder.append("<p style=\"margin:10px 0 0 0;color:#555555;font-size:12px;line-height:18px;word-break:break-word;\">")
                        .append(qrCodeService.buildMobileAccessUrl(ticket.getMobileAccessToken()))
                        .append("</p>");
                builder.append("</td>");
                builder.append("</tr>");
                builder.append("</table>");
                builder.append("</td></tr></table>");
                builder.append("</td></tr>");
            }
        }

        builder.append("<tr><td style=\"padding:4px 24px 24px 24px;font-size:12px;line-height:18px;color:#666666;\">");
        builder.append("<p style=\"margin:0;\">Conserva este correo para acceder a tus entradas el dia de la visita.</p>");
        builder.append("</td></tr>");
        builder.append("</table>");
        builder.append("</td></tr></table>");
        builder.append("</body></html>");
        return builder.toString();
    }

    private String entryQrId(Ticket ticket) {
        return "entry-" + ticket.getId();
    }

    private String mobileQrId(Ticket ticket) {
        return "mobile-" + ticket.getId();
    }
}
