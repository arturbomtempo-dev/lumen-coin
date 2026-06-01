package br.pucminas.lumen_coin_api.whatsapp.dto;

public record WhatsAppSendTextRequest(
        String chatId,
        String text,
        String session) {
}
