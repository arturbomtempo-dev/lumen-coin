package br.pucminas.lumen_coin_api.user.dto.response;

import br.pucminas.lumen_coin_api.user.enums.Avatar;

import java.time.Instant;
import java.util.UUID;

public record StudentResponse(
        UUID id,
        String name,
        String email,
        String phone,
        Avatar avatar,
        String cpf,
        String rg,
        String zipCode,
        String address,
        int balance,
        UUID institutionId,
        UUID courseId,
        Instant createdAt,
        Instant updatedAt) {
}
