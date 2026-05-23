package com.example.ZapSender.controller;

import com.example.ZapSender.config.ApiConfig;
import com.example.ZapSender.service.WebHookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebHookController {

    @Autowired
    private WebHookService webHookService;

    @Autowired
    private ApiConfig apiConfig;

    @PostMapping("/send-template")
    public ResponseEntity<String> sendTemplate(
            @RequestParam String destinationNumber,
            @RequestParam String templateName,
            @RequestParam(defaultValue = "en_US") String languageCode) {
        try {
            String result = webHookService.sendTemplate(destinationNumber, templateName, languageCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending template: " + e.getMessage());
        }
    }

    @PostMapping("/send-text")
    public ResponseEntity<String> sendTextMessage(
            @RequestParam String destinationNumber,
            @RequestParam String text) {
        try {
            String result = webHookService.sendTextMessage(destinationNumber, text);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending text message: " + e.getMessage());
        }
    }

    @PostMapping("/send-buttons")
    public ResponseEntity<String> sendButtonQuestion(
            @RequestParam String destinationNumber) {
        try {
            String result = webHookService.sendButtonQuestion(destinationNumber);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending button question: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<String> validateWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {

        if ("subscribe".equals(mode) && apiConfig.getVerifyToken().equals(token)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(403).body("Invalid token");
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        try {
            webHookService.processWebhook(payload);
            return ResponseEntity.ok("{\"status\": \"ok\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }

}
