package br.pucminas.lumen_coin_api.email.dto;

public record BenefitRedemptionStudentConfirmationEmailContext(
        String studentName,
        String benefitName,
        String couponCode
) {
}
