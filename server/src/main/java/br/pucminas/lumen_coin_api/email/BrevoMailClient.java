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

    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final RestClient restClient = RestClient.create();

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void send(String to, String subject, String htmlContent) {
        Map<String, Object> body = Map.of(
                "from", "Lumen Coin <" + fromEmail + ">",
                "to", List.of(to),
                "subject", subject,
                "html", htmlContent
        );

        restClient.post()
                .uri(RESEND_API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        log.debug("Email sent via Resend API to {}", to);
    }
}
