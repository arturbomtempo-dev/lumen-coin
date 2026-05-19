package br.pucminas.lumen_coin_api.email.dto;

public record PasswordResetEmailContext(
        String name,
        String resetLink,
        Integer expirationHours
) {
}
