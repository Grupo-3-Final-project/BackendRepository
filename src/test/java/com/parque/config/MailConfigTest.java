package com.parque.config;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;

class MailConfigTest {

    @Test
    void javaMailSender_shouldApplyConfiguredSmtpProperties() {
        MailConfig config = new MailConfig();

        JavaMailSender sender = config.javaMailSender(
                "smtp.example.com",
                587,
                "user@example.com",
                "secret",
                true,
                true,
                true,
                "smtp.example.com"
        );

        assertThat(sender).isInstanceOf(JavaMailSenderImpl.class);
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) sender;
        assertThat(mailSender.getHost()).isEqualTo("smtp.example.com");
        assertThat(mailSender.getPort()).isEqualTo(587);
        assertThat(mailSender.getUsername()).isEqualTo("user@example.com");
        assertThat(mailSender.getPassword()).isEqualTo("secret");
        assertThat(mailSender.getProtocol()).isEqualTo("smtp");
        assertThat(mailSender.getDefaultEncoding()).isEqualTo("UTF-8");
        assertThat(mailSender.getJavaMailProperties())
                .containsEntry("mail.smtp.auth", "true")
                .containsEntry("mail.smtp.starttls.enable", "true")
                .containsEntry("mail.smtp.starttls.required", "true")
                .containsEntry("mail.smtp.ssl.trust", "smtp.example.com")
                .containsEntry("mail.smtp.connectiontimeout", "10000")
                .containsEntry("mail.smtp.timeout", "10000")
                .containsEntry("mail.smtp.writetimeout", "10000");
    }

    @Test
    void javaMailSender_shouldAllowLocalSmtpWithoutAuthentication() {
        MailConfig config = new MailConfig();

        JavaMailSender sender = config.javaMailSender(
                "localhost",
                1025,
                "",
                "",
                false,
                false,
                false,
                "localhost"
        );

        assertThat(sender).isInstanceOf(JavaMailSenderImpl.class);
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) sender;
        assertThat(mailSender.getHost()).isEqualTo("localhost");
        assertThat(mailSender.getPort()).isEqualTo(1025);
        assertThat(mailSender.getUsername()).isNull();
        assertThat(mailSender.getPassword()).isNull();
        assertThat(mailSender.getJavaMailProperties())
                .containsEntry("mail.smtp.auth", "false")
                .containsEntry("mail.smtp.starttls.enable", "false")
                .containsEntry("mail.smtp.starttls.required", "false")
                .containsEntry("mail.smtp.ssl.trust", "localhost");
    }
}
