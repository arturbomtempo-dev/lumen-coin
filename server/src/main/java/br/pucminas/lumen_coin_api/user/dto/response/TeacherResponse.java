package br.pucminas.lumen_coin_api.user.dto.response;

import br.pucminas.lumen_coin_api.user.enums.Avatar;

import java.time.Instant;
import java.util.UUID;

public record TeacherResponse(
        UUID id,
        String name,
        String email,
        String phone,
        Avatar avatar,
        String cpf,
        String department,
        int balance,
        UUID institutionId,
        Instant createdAt,
        Instant updatedAt) {
}
