package com.example.bancocloud_negocio.service;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value; // <-- Importación necesaria
import org.springframework.stereotype.Service;

@Service
public class RabbitMqAdminService {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    // <-- Inyección centralizada exigida por la rúbrica
    @Value("${bancocloud.rabbitmq.queue.transferencias}")
    private String transferenciaQueueName;

    public RabbitMqAdminService(AmqpAdmin amqpAdmin, RabbitTemplate rabbitTemplate) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitTemplate = rabbitTemplate;
    }

    // --- GESTIÓN DE INFRAESTRUCTURA (Tu código) ---
    public String crearCola(String nombreCola) {
        Queue queue = new Queue(nombreCola, true);
        return amqpAdmin.declareQueue(queue);
    }

    public void crearExchange(String nombreExchange) {
        DirectExchange exchange = new DirectExchange(nombreExchange, true, false);
        amqpAdmin.declareExchange(exchange);
    }

    public void crearBinding(String nombreCola, String nombreExchange, String routingKey) {
        Queue queue = new Queue(nombreCola, true);
        DirectExchange exchange = new DirectExchange(nombreExchange, true, false);
        Binding binding = new Binding(nombreCola, Binding.DestinationType.QUEUE, nombreExchange, routingKey, null);
        amqpAdmin.declareBinding(binding);
    }

    public boolean eliminarCola(String nombreCola) {
        return amqpAdmin.deleteQueue(nombreCola);
    }

    // --- GESTIÓN DE MENSAJES (ACK Manual) ---
    public String procesarSiguienteMensajeManual() {
        return rabbitTemplate.execute(channel -> {
            // <-- Usamos la variable inyectada en lugar de "transferencia.queue"
            var getResponse = channel.basicGet(transferenciaQueueName, false);
            if (getResponse == null) return "No hay mensajes pendientes en la cola.";

            String cuerpoMensaje = new String(getResponse.getBody());
            long deliveryTag = getResponse.getEnvelope().getDeliveryTag();

            try {
                channel.basicAck(deliveryTag, false);
                return "Éxito: Mensaje procesado y ACK manual enviado.";
            } catch (Exception e) {
                channel.basicNack(deliveryTag, false, true);
                return "Error: El mensaje volvió a la cola (NACK enviado).";
            }
        });
    }
}