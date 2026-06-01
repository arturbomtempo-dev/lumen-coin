package br.pucminas.lumen_coin_api.whatsapp.service;

public interface WhatsAppService {

    void sendWelcome(String phone, String name);

    void sendTeacherWelcome(String phone, String name);

    void sendCoinSent(String phone, String teacherName, String studentName, int amount, String message);

    void sendCoinReceived(String phone, String studentName, String teacherName, int amount, String message);
}
