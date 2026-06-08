package br.pucminas.lumen_coin_api.benefit_redemption.messaging;

public record BenefitRedemptionMessage(
        String studentEmail,
        String studentName,
        String companyEmail,
        String companyName,
        String benefitName,
        String couponCode,
        int coinsSpent
) {
}
