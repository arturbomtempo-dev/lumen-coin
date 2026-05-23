package com.example.ZapSender.service;

import com.example.ZapSender.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class WebHookService {

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private RestTemplate restTemplate;

    private final Map<String, Object> questionSent = new HashMap<>();

    public String sendTemplate(String destinationNumber, String templateName, String languageCode) {
        Map<String, Object> language = new HashMap<>();
        language.put("code", languageCode);

        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);
        template.put("language", language);

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", destinationNumber);
        payload.put("type", "template");
        payload.put("template", template);

        return sendRequest(payload);
    }

    public String sendTextMessage(String destinationNumber, String text) {
        Map<String, Object> textBody = new HashMap<>();
        textBody.put("body", text);

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", destinationNumber);
        payload.put("type", "text");
        payload.put("text", textBody);

        return sendRequest(payload);
    }

    public String sendButtonQuestion(String destinationNumber) {
        Map<String, Object> body = new HashMap<>();
        body.put("text",
                "Hello! Tell me: what did you think about the Spring Boot workshop? Your opinion is very important!");

        List<Map<String, Object>> buttons = new ArrayList<>();
        buttons.add(createButton("option_great", "Great"));
        buttons.add(createButton("option_good", "Good"));
        buttons.add(createButton("option_average", "Average"));

        Map<String, Object> action = new HashMap<>();
        action.put("buttons", buttons);

        Map<String, Object> interactive = new HashMap<>();
        interactive.put("type", "button");
        interactive.put("body", body);
        interactive.put("action", action);

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", destinationNumber);
        payload.put("type", "interactive");
        payload.put("interactive", interactive);

        return sendRequest(payload);
    }

    private Map<String, Object> createButton(String id, String title) {
        Map<String, Object> reply = new HashMap<>();
        reply.put("id", id);
        reply.put("title", title);

        Map<String, Object> button = new HashMap<>();
        button.put("type", "reply");
        button.put("reply", reply);

        return button;
    }

    private String sendRequest(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiConfig.getAccessToken());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiConfig.getApiUrl(), request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return "Message sent successfully!\n" + response.getBody();
            } else {
                return "Error sending message.\nStatus: " + response.getStatusCode() + "\nResponse: "
                        + response.getBody();
            }
        } catch (Exception e) {
            return "Exception while sending message: " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    public void processWebhook(Map<String, Object> payload) {
        try {
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.getOrDefault("entry",
                    Collections.emptyList());

            for (Map<String, Object> entry : entries) {
                List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.getOrDefault("changes",
                        Collections.emptyList());
                for (Map<String, Object> change : changes) {
                    Map<String, Object> value = (Map<String, Object>) change.getOrDefault("value",
                            Collections.emptyMap());

                    if (value.containsKey("statuses"))
                        continue;

                    List<Map<String, Object>> messages = (List<Map<String, Object>>) value.getOrDefault("messages",
                            Collections.emptyList());
                    for (Map<String, Object> message : messages) {
                        String phone = (String) message.get("from");

                        Map<String, Object> interactive = (Map<String, Object>) message.getOrDefault("interactive",
                                Collections.emptyMap());
                        if (!interactive.isEmpty()) {
                            Map<String, Object> buttonReply = (Map<String, Object>) interactive
                                    .getOrDefault("button_reply", Collections.emptyMap());
                            if (!buttonReply.isEmpty()) {
                                String choice = (String) buttonReply.get("title");
                                String thankYouMessage = "You chose: " + choice
                                        + ". Thank you so much for your feedback!";
                                sendTextMessage(phone, thankYouMessage);
                                continue;
                            }
                        }

                        Map<String, Object> textMap = (Map<String, Object>) message.getOrDefault("text",
                                Collections.emptyMap());
                        String text = ((String) textMap.getOrDefault("body", "")).trim();

                        if (!questionSent.containsKey(phone)) {
                            String question = "Hello! Since we started the conversation, tell me: from 0 to 10, what did you think about the Spring Boot workshop? Your opinion is very important!";
                            sendTextMessage(phone, question);
                            questionSent.put(phone, true);
                            continue;
                        }

                        if (Boolean.TRUE.equals(questionSent.get(phone))) {
                            try {
                                int score = Integer.parseInt(text);
                                if (score >= 0 && score <= 10) {
                                    String thankYouMessage = "You chose: " + score
                                            + "! Thank you so much for your feedback. Feel free to reach out anytime!";
                                    sendTextMessage(phone, thankYouMessage);
                                    questionSent.put(phone, "answered");
                                } else {
                                    sendTextMessage(phone, "Please respond with a number between 0 and 10.");
                                }
                            } catch (NumberFormatException e) {
                                sendTextMessage(phone,
                                        "I didn't understand your response. Could you tell me your score (0 to 10) for the Spring Boot workshop?");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
