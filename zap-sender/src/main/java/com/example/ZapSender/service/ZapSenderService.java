package com.example.ZapSender.service;

import com.example.ZapSender.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ZapSenderService {

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private RestTemplate restTemplate;

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiConfig.getAccessToken());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiConfig.getApiUrl(), request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return "Template '" + templateName + "' sent successfully!\n" + response.getBody();
            } else {
                return "Error sending template.\nStatus: " + response.getStatusCode() + "\nResponse: "
                        + response.getBody();
            }

        } catch (Exception e) {
            return "Exception while sending template: " + e.getMessage();
        }
    }
}
