package br.pucminas.lumen_coin_api.user.dto.response;

import br.pucminas.lumen_coin_api.user.enums.Avatar;

import java.time.Instant;
import java.util.UUID;

public record CompanyResponse(
                UUID id,
                String name,
                String email,
                Avatar avatar,
                String cnpj,
                Instant createdAt,
                Instant updatedAt) {
}
