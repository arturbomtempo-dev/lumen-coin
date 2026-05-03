package br.pucminas.lumen_coin_api.auth.dto.response;

import br.pucminas.lumen_coin_api.user.enums.Avatar;
import br.pucminas.lumen_coin_api.user.enums.UserRole;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String name,
        String email,
        Avatar avatar,
        UserRole role) {
}
