package com.example.bancocloud_negocio.controller;

import com.example.bancocloud_negocio.dto.QueueRequestDto;
import com.example.bancocloud_negocio.service.RabbitMqAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // <-- Importación agregada para leer el JSON dinámico

@RestController
@RequestMapping("/api/admin/rabbitmq")
@CrossOrigin(origins = "http://localhost:4200") // Permiso para Angular
public class RabbitMqAdminController {

    private final RabbitMqAdminService rabbitMqAdminService;

    public RabbitMqAdminController(RabbitMqAdminService rabbitMqAdminService) {
        this.rabbitMqAdminService = rabbitMqAdminService;
    }

    // Endpoint para ACK Manual
    @PostMapping("/procesar-ack")
    public ResponseEntity<String> procesarMensajeManual() {
        return ResponseEntity.ok(rabbitMqAdminService.procesarSiguienteMensajeManual());
    }

    // Endpoint para Crear Cola
    @PostMapping("/colas")
    public ResponseEntity<String> crearCola(@Valid @RequestBody QueueRequestDto request) {
        String resultado = rabbitMqAdminService.crearCola(request.getName());
        return ResponseEntity.ok("Cola declarada exitosamente.");
    }

    // Endpoint para Eliminar Cola
    @DeleteMapping("/colas/{name}")
    public ResponseEntity<String> eliminarCola(@PathVariable String name) {
        boolean eliminada = rabbitMqAdminService.eliminarCola(name);
        if (eliminada) {
            return ResponseEntity.ok("Cola '" + name + "' eliminada exitosamente.");
        }
        return ResponseEntity.badRequest().body("No se pudo eliminar la cola. Verifica si existe.");
    }

    // --- NUEVOS ENDPOINTS PARA EXCHANGES Y BINDINGS (Requisito de la rúbrica) ---

    @PostMapping("/exchanges")
    public ResponseEntity<String> crearExchange(@RequestBody Map<String, String> request) {
        rabbitMqAdminService.crearExchange(request.get("name"));
        return ResponseEntity.ok("Exchange '" + request.get("name") + "' declarado exitosamente.");
    }

    @PostMapping("/bindings")
    public ResponseEntity<String> crearBinding(@RequestBody Map<String, String> request) {
        rabbitMqAdminService.crearBinding(
                request.get("cola"),
                request.get("exchange"),
                request.get("routingKey")
        );
        return ResponseEntity.ok("Binding creado exitosamente entre la cola '" + request.get("cola") + "' y el exchange '" + request.get("exchange") + "'.");
    }
}