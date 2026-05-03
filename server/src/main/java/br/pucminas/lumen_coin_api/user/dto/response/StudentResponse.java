package br.pucminas.lumen_coin_api.user.dto.response;

import br.pucminas.lumen_coin_api.user.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record StudentResponse(
                UUID id,
                String name,
                String email,
                String imagem,
                UserRole role,
                String cpf,
                String rg,
                String descricao,
                String endereco,
                int saldo,
                Instant createdAt,
                Instant updatedAt) {
}
