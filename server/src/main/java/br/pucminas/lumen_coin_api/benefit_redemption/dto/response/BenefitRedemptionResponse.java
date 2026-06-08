package br.pucminas.lumen_coin_api.benefit_redemption.dto.response;

import br.pucminas.lumen_coin_api.benefit_redemption.enums.RedemptionStatus;

import java.time.Instant;
import java.util.UUID;

public record BenefitRedemptionResponse(
        UUID id,
        UUID studentId,
        UUID benefitId,
        UUID companyId,
        UUID institutionId,
        String couponCode,
        int coinsSpent,
        RedemptionStatus status,
        Instant redeemedAt,
        Instant usedAt) {
}
