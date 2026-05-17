package br.pucminas.lumen_coin_api.coin_transfer.messaging;

import br.pucminas.lumen_coin_api.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoinTransferProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(CoinTransferMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COIN_TRANSFER_EXCHANGE,
                RabbitMQConfig.COIN_TRANSFER_ROUTING_KEY,
                message);
    }
}
