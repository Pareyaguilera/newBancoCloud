package com.example.bancocloud_negocio.repository;

import com.example.bancocloud_negocio.entity.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {
    // Buscar historial por cuenta de origen (Para el Cliente)
    List<Transferencia> findByCuentaOrigen(String cuentaOrigen);

    // Buscar transferencias por estado (Filtro para el Admin)
    List<Transferencia> findByEstado(String estado);
}


