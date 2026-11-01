package com.example.bancocloud_negocio.dto;

import jakarta.validation.constraints.NotBlank;

public class QueueRequestDto {
    @NotBlank(message = "El nombre de la cola no puede estar vacío")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
