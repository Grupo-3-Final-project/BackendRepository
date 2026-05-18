package com.parque.config;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;

class MailConfigTest {

    @Test
    void javaMailSender_shouldApplyConfiguredSmtpProperties() {
        MailConfig config = new MailConfig();

        JavaMailSender sender = config.javaMailSender("smtp.example.com", 587, "user@example.com", "secret");

        assertThat(sender).isInstanceOf(JavaMailSenderImpl.class);
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) sender;
        assertThat(mailSender.getHost()).isEqualTo("smtp.example.com");
        assertThat(mailSender.getPort()).isEqualTo(587);
        assertThat(mailSender.getUsername()).isEqualTo("user@example.com");
        assertThat(mailSender.getPassword()).isEqualTo("secret");
        assertThat(mailSender.getJavaMailProperties())
                .containsEntry("mail.smtp.auth", "true")
                .containsEntry("mail.smtp.starttls.enable", "true");
    }
}
