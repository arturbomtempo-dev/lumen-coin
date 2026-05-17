package br.pucminas.lumen_coin_api.coin_transfer.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CoinTransferResponse(
        UUID id,
        UUID senderId,
        String senderName,
        UUID recipientId,
        String recipientName,
        int amount,
        String message,
        Instant sentAt) {
}
