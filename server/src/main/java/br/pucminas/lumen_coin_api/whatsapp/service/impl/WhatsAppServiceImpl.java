package br.pucminas.lumen_coin_api.whatsapp.service.impl;

import br.pucminas.lumen_coin_api.whatsapp.dto.WhatsAppSendTextRequest;
import br.pucminas.lumen_coin_api.whatsapp.service.WhatsAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private final RestClient restClient;
    private final String session;
    private final boolean enabled;

    public WhatsAppServiceImpl(
            @Value("${waha.base-url}") String baseUrl,
            @Value("${waha.api-key}") String apiKey,
            @Value("${waha.session}") String session,
            @Value("${waha.enabled}") boolean enabled) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", apiKey)
                .build();
        this.session = session;
        this.enabled = enabled;
    }

    @Async
    @Override
    public void sendWelcome(String phone, String name) {
        if (!enabled || phone == null) {
            log.debug("WhatsApp disabled or phone null - skipping welcome message to {}", name);
            return;
        }
        String text = "Olá, " + name + "! Bem-vindo(a) ao Lumen Coin. " +
                "Aqui você pode acompanhar suas moedas e trocar por vantagens acadêmicas.";
        send(phone, text);
    }

    @Async
    @Override
    public void sendTeacherWelcome(String phone, String name) {
        if (!enabled || phone == null) {
            log.debug("WhatsApp disabled or phone null - skipping teacher welcome message to {}", name);
            return;
        }
        String text = "Olá, Prof. " + name + "! Bem-vindo(a) ao Lumen Coin. " +
                "Você já pode começar a reconhecer seus alunos com moedas virtuais. " +
                "Verifique seu e-mail para obter as credenciais de acesso.";
        send(phone, text);
    }

    @Async
    @Override
    public void sendCoinSent(String phone, String teacherName, String studentName, int amount, String message) {
        if (!enabled || phone == null) {
            log.debug("WhatsApp disabled or phone null - skipping coin sent message to {}", teacherName);
            return;
        }
        String text = "Olá, Prof. " + teacherName + "! Você enviou " + amount +
                " moeda(s) para " + studentName + " no Lumen Coin." +
                (message != null && !message.isBlank() ? " Mensagem: \"" + message + "\"." : "");
        send(phone, text);
    }

    @Async
    @Override
    public void sendCoinReceived(String phone, String studentName, String teacherName, int amount, String message) {
        if (!enabled || phone == null) {
            log.debug("WhatsApp disabled or phone null - skipping coin received message to {}", studentName);
            return;
        }
        String text = "Olá, " + studentName + "! Você recebeu " + amount +
                " moeda(s) de " + teacherName + " no Lumen Coin." +
                (message != null && !message.isBlank() ? " Mensagem: \"" + message + "\"." : "");
        send(phone, text);
    }

    private void send(String phone, String text) {
        try {
            String digits = phone.replaceAll("[^0-9]", "");
            String chatId = "55" + digits + "@c.us";
            WhatsAppSendTextRequest payload = new WhatsAppSendTextRequest(chatId, text, session);
            restClient.post()
                    .uri("/api/sendText")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            log.info("WhatsApp message sent to {}", chatId);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to {}: {}", phone, e.getMessage());
        }
    }
}
