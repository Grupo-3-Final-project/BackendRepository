package com.parque.booking.service.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("'${spring.mail.host:}'.trim().length() > 0")
public class MailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);

    private final JavaMailSender javaMailSender;
    private final String from;

    public MailSender(JavaMailSender javaMailSender, @Value("${spring.mail.username:}") String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    public boolean sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if (from != null && !from.isBlank()) {
            message.setFrom(from);
        }
        try {
            javaMailSender.send(message);
            return true;
        } catch (MailException exception) {
            LOGGER.warn("Mail delivery failed for {}", to, exception);
            return false;
        }
    }
}
