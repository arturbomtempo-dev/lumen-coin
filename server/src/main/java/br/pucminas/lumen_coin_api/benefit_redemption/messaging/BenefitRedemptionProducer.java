package br.pucminas.lumen_coin_api.benefit_redemption.messaging;

import br.pucminas.lumen_coin_api.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BenefitRedemptionProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(BenefitRedemptionMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BENEFIT_REDEMPTION_EXCHANGE,
                RabbitMQConfig.BENEFIT_REDEMPTION_ROUTING_KEY,
                message);
    }
}
