package br.pucminas.lumen_coin_api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeInitialPasswordRequest(
        @NotBlank @Size(min = 8, max = 72) String newPassword,
        @NotBlank String confirmPassword) {
}
