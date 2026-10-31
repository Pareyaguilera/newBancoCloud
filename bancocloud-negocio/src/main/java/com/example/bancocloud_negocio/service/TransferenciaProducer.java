package com.example.bancocloud_negocio.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TransferenciaProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${bancocloud.rabbitmq.exchange}")
    private String exchange;

    @Value("${bancocloud.rabbitmq.routing-key.transferencias}")
    private String routingKey;

    public TransferenciaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarTransferencia(String idTransferencia) {
        rabbitTemplate.convertAndSend(exchange, routingKey, idTransferencia);
        System.out.println(" [x] Transferencia enviada a la cola: " + idTransferencia);
    }
}
