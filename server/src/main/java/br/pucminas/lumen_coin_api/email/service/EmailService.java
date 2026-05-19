package br.pucminas.lumen_coin_api.email.service;

public interface EmailService {

    void sendWelcome(String to, String name);

    void sendPasswordReset(String to, String name, String resetLink, Integer expirationHours);

    void sendCoinSent(String to, String teacherName, String studentName, int amount, String message);

    void sendCoinReceived(String to, String studentName, String teacherName, int amount, String message);
}
