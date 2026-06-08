package br.pucminas.lumen_coin_api.email.dto;

public record BenefitRedemptionStudentApprovedEmailContext(
        String studentName,
        String benefitName,
        String usageNotes
) {
}
