package com.example.bancocloud_negocio.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TransferenciaConsumer {

    // Leemos el nombre de la cola centralizada desde el properties
    @RabbitListener(queues = "${bancocloud.rabbitmq.queue.transferencias}")
    public void recibirTransferencia(String idTransferencia, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            System.out.println(" [v] Procesando transferencia ID: " + idTransferencia);

            // Simulamos un error si el ID es "999" para probar que la DLQ funciona
            if ("999".equals(idTransferencia)) {
                throw new RuntimeException("Error simulado de saldo insuficiente");
            }

            // Aquí luego irá la lógica para actualizar saldo en BD...

            // Confirmación manual exitosa (ACK)
            channel.basicAck(tag, false);
            System.out.println(" [v] Transferencia procesada y confirmada con ACK.");

        } catch (Exception e) {
            System.err.println(" [X] Error al procesar transferencia: " + e.getMessage());

            // Rechazo manual (NACK). El último "false" evita que se re-encole y lo envía a la DLQ.
            channel.basicNack(tag, false, false);
            System.err.println(" [X] Mensaje derivado a la Dead Letter Queue (DLQ).");
        }
    }
}
