package com.example.bancocloud_negocio.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cuenta_origen", nullable = false)
    private String cuentaOrigen;

    @Column(name = "cuenta_destino", nullable = false)
    private String cuentaDestino;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "usuario_sub", nullable = false)
    private String usuarioSub;

    @Column(nullable = false)
    private String estado = "PENDIENTE"; // El estado clave para RabbitMQ

    @Column(name = "fecha", insertable = false, updatable = false)
    private LocalDateTime fecha;

    // Genera los Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCuentaOrigen() { return cuentaOrigen; }
    public void setCuentaOrigen(String cuentaOrigen) { this.cuentaOrigen = cuentaOrigen; }
    public String getCuentaDestino() { return cuentaDestino; }
    public void setCuentaDestino(String cuentaDestino) { this.cuentaDestino = cuentaDestino; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getUsuarioSub() { return usuarioSub; }
    public void setUsuarioSub(String usuarioSub) { this.usuarioSub = usuarioSub; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
