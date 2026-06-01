package br.pucminas.lumen_coin_api.coin_transfer.messaging;

import java.util.UUID;

public record CoinTransferMessage(
                UUID recipientId,
                int amount,
                String recipientEmail,
                String recipientName,
                String senderName,
                String transferMessage,
                String recipientPhone) {
}
