package br.pucminas.lumen_coin_api.benefit_redemption.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UseBenefitRedemptionRequest(
        @NotBlank String couponCode) {
}
