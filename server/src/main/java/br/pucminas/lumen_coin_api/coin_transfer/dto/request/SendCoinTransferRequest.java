package br.pucminas.lumen_coin_api.coin_transfer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SendCoinTransferRequest(
        @NotNull UUID studentId,
        @Min(1) int amount,
        @NotBlank @Size(max = 500) String message) {
}
