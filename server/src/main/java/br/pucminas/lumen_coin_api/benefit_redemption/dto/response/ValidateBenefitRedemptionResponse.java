package br.pucminas.lumen_coin_api.benefit_redemption.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ValidateBenefitRedemptionResponse(
        UUID id,
        String studentName,
        String benefitName,
        int coinsSpent,
        Instant redeemedAt,
        String couponCode
) {
}
