package br.pucminas.lumen_coin_api.email.service.impl;

import br.pucminas.lumen_coin_api.email.dto.CoinReceivedEmailContext;
import br.pucminas.lumen_coin_api.email.dto.CoinSentEmailContext;
import br.pucminas.lumen_coin_api.email.dto.PasswordResetEmailContext;
import br.pucminas.lumen_coin_api.email.dto.TeacherWelcomeEmailContext;
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
            log.debug("Mail disabled - skipping welcome email to {}", to);
            return;
        }
        try {
            String html = templateEngine.render("welcome", new WelcomeEmailContext(name));
            send(to, "Bem-vindo ao Lumen Coin, " + name + "!", html);
            log.info("Welcome email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendTeacherWelcome(String to, String name, String generatedPassword) {
        if (!enabled) {
            log.debug("Mail disabled - skipping teacher welcome email to {}", to);
            return;
        }
        try {
            String html = templateEngine.render("teacher-welcome",
                    new TeacherWelcomeEmailContext(name, to, generatedPassword));
            send(to, "Bem-vindo ao Lumen Coin, Prof. " + name + "!", html);
            log.info("Teacher welcome email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send teacher welcome email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendPasswordReset(String to, String name, String resetLink, Integer expirationHours) {
        if (!enabled) {
            log.debug("Mail disabled - skipping password reset email to {}", to);
            return;
        }
        try {
            String html = templateEngine.render("password-reset",
                    new PasswordResetEmailContext(name, resetLink, expirationHours));
            send(to, "Recuperação de senha - Lumen Coin", html);
            log.info("Password reset email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendCoinSent(String to, String teacherName, String studentName, int amount, String message) {
        if (!enabled) {
            log.debug("Mail disabled - skipping coin sent email to {}", to);
            return;
        }
        try {
            String html = templateEngine.render("coin-sent",
                    new CoinSentEmailContext(teacherName, studentName, amount, message));
            send(to, "Moedas enviadas com sucesso - Lumen Coin", html);
            log.info("Coin sent email dispatched to {}", to);
        } catch (Exception e) {
            log.error("Failed to send coin sent email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendCoinReceived(String to, String studentName, String teacherName, int amount, String message) {
        if (!enabled) {
            log.debug("Mail disabled - skipping coin received email to {}", to);
            return;
        }
        try {
            String html = templateEngine.render("coin-received",
                    new CoinReceivedEmailContext(studentName, teacherName, amount, message));
            send(to, "Você recebeu moedas! - Lumen Coin", html);
            log.info("Coin received email dispatched to {}", to);
        } catch (Exception e) {
            log.error("Failed to send coin received email to {}: {}", to, e.getMessage());
        }
    }

    private void send(String to, String subject, String html) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }
}
