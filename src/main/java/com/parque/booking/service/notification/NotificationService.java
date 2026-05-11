package com.parque.booking.service.notification;

import com.parque.booking.dto.BookingResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnExpression("'${spring.mail.host:}'.trim().length() > 0")
public class NotificationService {

    private final MailSender mailSender;

    public NotificationService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendBookingConfirmation(List<String> emails, BookingResponse booking) {
        if (booking == null) {
            return false;
        }

        if (emails == null || emails.isEmpty()) {
            return false;
        }

        String subject = "Confirmacion de reserva - La Ultima Puerta";
        String body = buildBody(booking);

        List<String> validEmails = emails.stream()
                .filter(email -> email != null && !email.isBlank())
                .toList();

        if (validEmails.isEmpty()) {
            return false;
        }

        return validEmails.stream()
                .allMatch(email -> mailSender.sendEmail(email, subject, body));
    }

    private String buildBody(BookingResponse booking) {
        return """
                Tu reserva se ha realizado correctamente.

                Numero de reserva: %s
                Fecha de visita: %s
                Hotel: %s
                Tipo de pension: %s
                Precio total: %s EUR
                """.formatted(
                booking.id(),
                booking.visitDate(),
                booking.hotelName() == null ? "Sin hotel" : booking.hotelName(),
                booking.boardType(),
                booking.totalPrice());
    }
}
