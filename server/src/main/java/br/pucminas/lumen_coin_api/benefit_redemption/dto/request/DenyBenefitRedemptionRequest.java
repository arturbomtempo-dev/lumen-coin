package br.pucminas.lumen_coin_api.benefit_redemption.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DenyBenefitRedemptionRequest(
        @NotBlank String couponCode,
        @NotBlank @Size(max = 2000) String denialReason
) {
}
