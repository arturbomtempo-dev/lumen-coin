package br.pucminas.lumen_coin_api.user.dto.response;

import br.pucminas.lumen_coin_api.user.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record CompanyResponse(
                UUID id,
                String name,
                String email,
                String imagem,
                UserRole role,
                String cnpj,
                String descricao,
                Instant createdAt,
                Instant updatedAt) {
}
