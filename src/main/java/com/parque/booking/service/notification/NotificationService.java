package com.parque.booking.service.notification;

import com.parque.booking.dto.BookingResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnBean(JavaMailSender.class)
public class NotificationService {

    private final MailSender mailSender;

    public NotificationService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(List<String> emails, BookingResponse booking) {
        if (booking == null) {
            return;
        }

        if (emails == null || emails.isEmpty()) {
            return;
        }

        String subject = "Confirmacion de reserva - La Ultima Puerta";
        String body = buildBody(booking);

        emails.stream()
                .filter(email -> email != null && !email.isBlank())
                .forEach(email -> mailSender.sendEmail(email, subject, body));
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
