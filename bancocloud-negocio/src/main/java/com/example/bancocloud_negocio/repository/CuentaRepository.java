package com.example.bancocloud_negocio.repository;

import com.example.bancocloud_negocio.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
}
