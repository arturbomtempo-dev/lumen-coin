package br.pucminas.lumen_coin_api.email.dto;

public record CoinSentEmailContext(
        String teacherName,
        String studentName,
        int amount,
        String message
) {
}
