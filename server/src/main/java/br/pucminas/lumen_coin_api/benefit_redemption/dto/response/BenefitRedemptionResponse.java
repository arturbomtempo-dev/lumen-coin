package br.pucminas.lumen_coin_api.benefit_redemption.dto.response;

import br.pucminas.lumen_coin_api.benefit_redemption.enums.RedemptionStatus;

import java.time.Instant;
import java.util.UUID;

public record BenefitRedemptionResponse(
        UUID id,
        UUID studentId,
        String studentName,
        UUID benefitId,
        String benefitName,
        UUID companyId,
        String companyName,
        UUID institutionId,
        String institutionName,
        String couponCode,
        int coinsSpent,
        RedemptionStatus status,
        Instant redeemedAt,
        Instant usedAt) {
}
