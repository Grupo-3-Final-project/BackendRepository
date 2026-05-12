package com.parque.booking.service.notification;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.ByteArrayResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public boolean sendEmail(String to, String subject, String htmlBody, Map<String, byte[]> inlineImages) {
        try {
            var message = javaMailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (from != null && !from.isBlank()) {
                helper.setFrom(from);
            }

            if (inlineImages != null) {
                for (Map.Entry<String, byte[]> image : inlineImages.entrySet()) {
                    helper.addInline(image.getKey(), new ByteArrayResource(image.getValue()), "image/png");
                }
            }

            javaMailSender.send(message);
            return true;
        } catch (MailException | MessagingException exception) {
            LOGGER.warn("Mail delivery failed for {}", to, exception);
            return false;
        }
    }
}
