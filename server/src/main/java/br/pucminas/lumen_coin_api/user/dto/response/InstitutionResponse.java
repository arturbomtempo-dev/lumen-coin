package br.pucminas.lumen_coin_api.user.dto.response;

import br.pucminas.lumen_coin_api.user.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record InstitutionResponse(
                UUID id,
                String name,
                String email,
                String imagem,
                UserRole role,
                String cnpj,
                String cep,
                String endereco,
                Instant createdAt,
                Instant updatedAt) {
}
