package com.SistemaPago.SistemaPago.repository;

import com.SistemaPago.SistemaPago.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Marcamos esta interfaz como un repositorio
public interface PaymentRepository extends JpaRepository<Payment, Long> { // Hereda de JpaRepository
}
