package br.pucminas.lumen_coin_api.email.dto;

public record CoinReceivedEmailContext(
        String studentName,
        String teacherName,
        int amount,
        String message
) {
}
