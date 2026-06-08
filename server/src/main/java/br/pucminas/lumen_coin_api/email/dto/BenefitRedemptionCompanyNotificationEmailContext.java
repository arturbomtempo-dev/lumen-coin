package br.pucminas.lumen_coin_api.email.dto;

public record BenefitRedemptionCompanyNotificationEmailContext(
        String companyName,
        String studentName,
        String benefitName,
        String couponCode
) {
}
