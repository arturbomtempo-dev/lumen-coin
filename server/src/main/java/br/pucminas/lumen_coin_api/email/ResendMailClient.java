package br.pucminas.lumen_coin_api.email;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ResendMailClient {

    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final RestClient restClient = RestClient.create();

    @Value("${resend.api-key:}")
    private String resendApiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    // Injetado apenas quando spring.mail.host está configurado (desenvolvimento local)
    @Autowired(required = false)
    private JavaMailSender smtpSender;

    public void send(String to, String subject, String htmlContent) {
        if (!resendApiKey.isBlank()) {
            sendViaResend(to, subject, htmlContent);
        } else if (smtpSender != null) {
            sendViaSmtp(to, subject, htmlContent);
        } else {
            throw new IllegalStateException(
                    "Nenhum provedor de e-mail configurado. Defina RESEND_API_KEY (produção) ou MAIL_HOST (desenvolvimento).");
        }
    }

    private void sendViaResend(String to, String subject, String htmlContent) {
        Map<String, Object> body = Map.of(
                "from", "Lumen Coin <" + fromEmail + ">",
                "to", List.of(to),
                "subject", subject,
                "html", htmlContent
        );

        restClient.post()
                .uri(RESEND_API_URL)
                .header("Authorization", "Bearer " + resendApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        log.debug("Email sent via Resend to {}", to);
    }

    private void sendViaSmtp(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = smtpSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            smtpSender.send(message);
            log.debug("Email sent via SMTP to {}", to);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar e-mail via SMTP: " + e.getMessage(), e);
        }
    }
}
