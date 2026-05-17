package br.pucminas.lumen_coin_api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COIN_TRANSFER_QUEUE = "coin.transfer";
    public static final String COIN_TRANSFER_DLQ = "coin.transfer.dlq";
    public static final String COIN_TRANSFER_EXCHANGE = "coin.transfer.exchange";
    public static final String COIN_TRANSFER_ROUTING_KEY = "coin.transfer";

    @Bean
    Queue coinTransferQueue() {
        return QueueBuilder.durable(COIN_TRANSFER_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", COIN_TRANSFER_DLQ)
                .build();
    }

    @Bean
    Queue coinTransferDeadLetterQueue() {
        return QueueBuilder.durable(COIN_TRANSFER_DLQ).build();
    }

    @Bean
    DirectExchange coinTransferExchange() {
        return new DirectExchange(COIN_TRANSFER_EXCHANGE);
    }

    @Bean
    Binding coinTransferBinding(Queue coinTransferQueue, DirectExchange coinTransferExchange) {
        return BindingBuilder.bind(coinTransferQueue).to(coinTransferExchange).with(COIN_TRANSFER_ROUTING_KEY);
    }

    @Bean
    MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter());
        return template;
    }
}
