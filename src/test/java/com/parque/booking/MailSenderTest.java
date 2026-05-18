package com.parque.booking;

import com.parque.booking.service.notification.MailSender;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void shouldBuildAndSendHtmlMessageWithInlineImages() throws Exception {
        MailSender mailSender = new MailSender(javaMailSender, "taquilla@parque.local");

        boolean sent = mailSender.sendEmail(
                "cliente@example.com",
                "Reserva",
                "<html><body>ok</body></html>",
                Map.of("entry-1", new byte[]{1, 2, 3})
        );

        assertThat(sent).isTrue();

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getAllRecipients()[0].toString()).isEqualTo("cliente@example.com");
        assertThat(sentMessage.getFrom()[0].toString()).isEqualTo("taquilla@parque.local");
        assertThat(sentMessage.getSubject()).isEqualTo("Reserva");
        assertThat(sentMessage.getContent()).isNotNull();
    }

    @Test
    void shouldReturnFalseWhenMailSenderThrowsException() {
        MailSender mailSender = new MailSender(javaMailSender, "");
        doThrow(new MailSendException("boom")).when(javaMailSender).send(mimeMessage);

        boolean sent = mailSender.sendEmail(
                "cliente@example.com",
                "Reserva",
                "<html><body>ok</body></html>",
                Map.of()
        );

        assertThat(sent).isFalse();
    }
}
