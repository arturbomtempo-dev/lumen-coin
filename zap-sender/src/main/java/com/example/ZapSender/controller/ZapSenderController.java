package com.example.ZapSender.controller;

import com.example.ZapSender.service.ZapSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zapsender")
public class ZapSenderController {

    @Autowired
    private ZapSenderService zapSenderService;

    @PostMapping("/send-template")
    public ResponseEntity<String> sendTemplate(
            @RequestParam String destinationNumber,
            @RequestParam String templateName,
            @RequestParam(defaultValue = "en_US") String languageCode) {

        try {
            String result = zapSenderService.sendTemplate(destinationNumber, templateName, languageCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending template: " + e.getMessage());
        }
    }
}
