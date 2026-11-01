package com.example.bancocloud_negocio.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TransferenciaConsumer {

    // LÍNEA DESCOMENTADA para cumplir con la rúbrica
    @RabbitListener(queues = "${bancocloud.rabbitmq.queue.transferencias}")
    public void recibirTransferencia(String idTransferencia, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            System.out.println(" [v] Procesando transferencia ID: " + idTransferencia);

            if ("999".equals(idTransferencia)) {
                throw new RuntimeException("Error simulado de saldo insuficiente");
            }

            channel.basicAck(tag, false);
            System.out.println(" [v] Transferencia procesada y confirmada con ACK.");

        } catch (Exception e) {
            System.err.println(" [X] Error al procesar transferencia: " + e.getMessage());
            channel.basicNack(tag, false, false);
            System.err.println(" [X] Mensaje derivado a la Dead Letter Queue (DLQ).");
        }
    }
}
