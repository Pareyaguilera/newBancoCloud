package com.example.bancocloud_negocio.controller;

import com.example.bancocloud_negocio.dto.LoginRequest;
import com.example.bancocloud_negocio.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthenticationResultType resultado = authService.autenticarEnCognito(request.getUsername(), request.getPassword());

            // Mapeamos los tokens manualmente a un Diccionario (JSON)
            Map<String, String> tokens = new HashMap<>();
            tokens.put("idToken", resultado.idToken());
            tokens.put("accessToken", resultado.accessToken());
            tokens.put("refreshToken", resultado.refreshToken());

            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}