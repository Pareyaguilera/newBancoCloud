package com.example.bancocloud_negocio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferenciaRequestDto {

    @NotBlank(message = "La cuenta de origen es obligatoria")
    private String cuentaOrigen;

    @NotBlank(message = "La cuenta de destino es obligatoria")
    private String cuentaDestino;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotBlank(message = "El usuario es obligatorio")
    private String usuarioSub;

    // Getters y Setters
    public String getCuentaOrigen() { return cuentaOrigen; }
    public void setCuentaOrigen(String cuentaOrigen) { this.cuentaOrigen = cuentaOrigen; }
    public String getCuentaDestino() { return cuentaDestino; }
    public void setCuentaDestino(String cuentaDestino) { this.cuentaDestino = cuentaDestino; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getUsuarioSub() { return usuarioSub; }
    public void setUsuarioSub(String usuarioSub) { this.usuarioSub = usuarioSub; }
}
