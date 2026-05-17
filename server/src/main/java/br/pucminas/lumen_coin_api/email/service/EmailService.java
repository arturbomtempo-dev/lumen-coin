package br.pucminas.lumen_coin_api.email.service;

public interface EmailService {

    void sendWelcome(String to, String name);
}
