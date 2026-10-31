package com.example.bancocloud_negocio.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Leemos las variables centralizadas del application.yml
    @Value("${bancocloud.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${bancocloud.rabbitmq.queue.transferencias}")
    private String queueTransferenciasName;

    @Value("${bancocloud.rabbitmq.routing-key.transferencias}")
    private String routingKeyTransferencias;

    @Value("${bancocloud.rabbitmq.queue.dlq}")
    private String queueDlqName;

    @Value("${bancocloud.rabbitmq.routing-key.dlq}")
    private String routingKeyDlq;

    // 1. Declaramos el Exchange Principal
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    // 2. Declaramos la Cola DLQ (Donde irán los errores)
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(queueDlqName).build();
    }

    // 3. Declaramos la Cola Principal vinculada a la DLQ
    @Bean
    public Queue transferenciasQueue() {
        return QueueBuilder.durable(queueTransferenciasName)
                .withArgument("x-dead-letter-exchange", exchangeName)
                .withArgument("x-dead-letter-routing-key", routingKeyDlq)
                .build();
    }

    // 4. Bindeamos la Cola Principal al Exchange
    @Bean
    public Binding bindingTransferencias() {
        return BindingBuilder.bind(transferenciasQueue()).to(exchange()).with(routingKeyTransferencias);
    }

    // 5. Bindeamos la Cola DLQ al Exchange
    @Bean
    public Binding bindingDlq() {
        return BindingBuilder.bind(dlqQueue()).to(exchange()).with(routingKeyDlq);
    }
}
