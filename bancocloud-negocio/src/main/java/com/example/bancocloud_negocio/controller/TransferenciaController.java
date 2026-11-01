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
@CrossOrigin(origins = "http://localhost:4200") // <-- Permiso CORS agregado aquí
public class TransferenciaController {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaProducer transferenciaProducer;
    private final RabbitTemplate rabbitTemplate;

    public TransferenciaController(TransferenciaRepository transferenciaRepository,
                                   TransferenciaProducer transferenciaProducer,
                                   RabbitTemplate rabbitTemplate) {
        this.transferenciaRepository = transferenciaRepository;
        this.transferenciaProducer = transferenciaProducer;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<String> iniciarTransferencia(@Valid @RequestBody TransferenciaRequestDto request) {
        Transferencia transferencia = new Transferencia();
        transferencia.setCuentaOrigen(request.getCuentaOrigen());
        transferencia.setCuentaDestino(request.getCuentaDestino());
        transferencia.setMonto(request.getMonto());
        transferencia.setUsuarioSub(request.getUsuarioSub());

        Transferencia guardada = transferenciaRepository.save(transferencia);

        transferenciaProducer.enviarTransferencia(guardada.getId().toString());

        return ResponseEntity.ok("Transferencia en proceso. ID Ticket: " + guardada.getId());
    }

    @GetMapping("/historial/{cuenta}")
    public ResponseEntity<?> obtenerHistorial(@PathVariable String cuenta) {
        List<Transferencia> historial = transferenciaRepository.findByCuentaOrigen(cuenta);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/admin/todas")
    public ResponseEntity<?> listarTodas(@RequestParam(required = false) String estado) {
        if (estado != null && !estado.isEmpty()) {
            return ResponseEntity.ok(transferenciaRepository.findByEstado(estado));
        }
        return ResponseEntity.ok(transferenciaRepository.findAll());
    }

    @PutMapping("/admin/anular/{id}")
    public ResponseEntity<?> anularTransferencia(@PathVariable Integer id) {
        Optional<Transferencia> txOptional = transferenciaRepository.findById(id);

        if (txOptional.isPresent()) {
            Transferencia tx = txOptional.get();
            tx.setEstado("ANULADA");
            transferenciaRepository.save(tx);

            transferenciaProducer.enviarTransferencia(tx.getId().toString());

            return ResponseEntity.ok("Transferencia " + id + " anulada exitosamente y enviada a la cola para reversar saldo.");
        }
        return ResponseEntity.notFound().build();
    }
}