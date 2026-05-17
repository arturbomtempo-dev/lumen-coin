package br.pucminas.lumen_coin_api.email.service.impl;

import br.pucminas.lumen_coin_api.email.dto.WelcomeEmailContext;
import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.email.template.HandlebarsTemplateEngine;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final HandlebarsTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.enabled}")
    private boolean enabled;

    @Async
    @Override
    public void sendWelcome(String to, String name) {
        if (!enabled) {
            log.debug("Mail disabled — skipping welcome email to {}", to);
            return;
        }
        try {
            String html = templateEngine.render("welcome", new WelcomeEmailContext(name));
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Bem-vindo ao Lumen Coin, " + name + "!");
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Welcome email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }
}
