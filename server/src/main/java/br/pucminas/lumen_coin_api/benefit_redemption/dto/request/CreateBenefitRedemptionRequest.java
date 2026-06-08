package br.pucminas.lumen_coin_api.benefit_redemption.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBenefitRedemptionRequest(
        @NotNull UUID benefitId) {
}
