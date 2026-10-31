package com.example.bancocloud_negocio.controller;

import com.example.bancocloud_negocio.dto.TransferenciaRequestDto;
import com.example.bancocloud_negocio.entity.Transferencia;
import com.example.bancocloud_negocio.repository.TransferenciaRepository;
import com.example.bancocloud_negocio.service.TransferenciaProducer;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaController {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaProducer transferenciaProducer;
    private final RabbitTemplate rabbitTemplate; // Necesario para enviar el evento de anulación

    // Constructor actualizado con RabbitTemplate
    public TransferenciaController(TransferenciaRepository transferenciaRepository,
                                   TransferenciaProducer transferenciaProducer,
                                   RabbitTemplate rabbitTemplate) {
        this.transferenciaRepository = transferenciaRepository;
        this.transferenciaProducer = transferenciaProducer;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<String> iniciarTransferencia(@Valid @RequestBody TransferenciaRequestDto request) {
        // 1. Guardamos en MySQL con estado "PENDIENTE" por defecto
        Transferencia transferencia = new Transferencia();
        transferencia.setCuentaOrigen(request.getCuentaOrigen());
        transferencia.setCuentaDestino(request.getCuentaDestino());
        transferencia.setMonto(request.getMonto());
        transferencia.setUsuarioSub(request.getUsuarioSub());
        // El estado y la fecha se asignan automáticamente por la entidad / BD

        Transferencia guardada = transferenciaRepository.save(transferencia);

        // 2. Enviamos el ID a RabbitMQ para que el consumidor lo procese de forma asíncrona
        transferenciaProducer.enviarTransferencia(guardada.getId().toString());

        return ResponseEntity.ok("Transferencia en proceso. ID Ticket: " + guardada.getId());
    }

    // 1. Consultar historial propio (Cliente)
    @GetMapping("/historial/{cuenta}")
    public ResponseEntity<?> obtenerHistorial(@PathVariable String cuenta) {
        List<Transferencia> historial = transferenciaRepository.findByCuentaOrigen(cuenta);
        return ResponseEntity.ok(historial);
    }

    // 2. Listar transferencias con filtros (Admin)
    @GetMapping("/admin/todas")
    public ResponseEntity<?> listarTodas(@RequestParam(required = false) String estado) {
        if (estado != null && !estado.isEmpty()) {
            return ResponseEntity.ok(transferenciaRepository.findByEstado(estado));
        }
        return ResponseEntity.ok(transferenciaRepository.findAll());
    }

    // 3. Anular transferencia (Admin)
    @PutMapping("/admin/anular/{id}")
    public ResponseEntity<?> anularTransferencia(@PathVariable Integer id) {
        Optional<Transferencia> txOptional = transferenciaRepository.findById(id);

        if (txOptional.isPresent()) {
            Transferencia tx = txOptional.get();
            tx.setEstado("ANULADA");
            transferenciaRepository.save(tx);

            // Reutilizamos el producer que ya tiene el exchange y routing key correctos
            transferenciaProducer.enviarTransferencia(tx.getId().toString());

            return ResponseEntity.ok("Transferencia " + id + " anulada exitosamente y enviada a la cola para reversar saldo.");
        }
        return ResponseEntity.notFound().build();
    }
}