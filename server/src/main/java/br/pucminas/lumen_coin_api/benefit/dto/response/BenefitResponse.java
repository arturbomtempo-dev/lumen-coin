package br.pucminas.lumen_coin_api.benefit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record BenefitResponse(
        UUID id,
        UUID companyId,
        String name,
        String description,
        String imageUrl,
        int cost,
        Instant createdAt,
        Instant updatedAt) {
}