package br.pucminas.lumen_coin_api.benefit_redemption.dto.response;

import java.util.Set;
import java.util.UUID;

public record RedeemedBenefitIdsResponse(
        Set<UUID> pendingBenefitIds,
        Set<UUID> usedBenefitIds
) {
}
