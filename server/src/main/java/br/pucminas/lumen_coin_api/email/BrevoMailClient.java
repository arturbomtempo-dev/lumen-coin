package br.pucminas.lumen_coin_api.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BrevoMailClient {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient = RestClient.create();

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void send(String to, String subject, String htmlContent) {
        Map<String, Object> body = Map.of(
                "sender", Map.of("email", fromEmail, "name", "Lumen Coin"),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "htmlContent", htmlContent
        );

        restClient.post()
                .uri(BREVO_API_URL)
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        log.debug("Email sent via Brevo API to {}", to);
    }
}
