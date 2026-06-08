package br.pucminas.lumen_coin_api.email.dto;

public record BenefitRedemptionStudentDeniedEmailContext(
        String studentName,
        String benefitName,
        String denialReason
) {
}
