package br.pucminas.lumen_coin_api.email.dto;

public record BenefitRedemptionStudentQrScanEmailContext(
        String studentName,
        String benefitName,
        String companyName
) {
}
