package com.SistemaPago.SistemaPago.controller;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/payments") // Mapea las solicitudes a /api/payments
public class PaymentController {
    @Autowired // Inyectamos el servicio de pagos
    private PaymentService paymentService;

    @PostMapping // Mapea solicitudes POST para registrar pagos
    public ResponseEntity<PaymentDTO> registerPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            PaymentDTO createdPayment = paymentService.registerPayment(paymentDTO); // Intentamos registrar el pago
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment); // Devolvemos 201 (CREATED) si tiene éxito
        } catch (PaymentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Devolvemos 500 si hay un error
        }
    }

    @GetMapping // Mapea solicitudes GET para obtener todos los pagos
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        try {
            List<PaymentDTO> payments = paymentService.getAllPayments(); // Intentamos obtener todos los pagos
            return ResponseEntity.ok(payments.stream()  // Devolvemos 200 (OK) con la lista de pagos
                    .map(payment -> {
                        payment.setCardNumber(payment.getMaskedCardNumber()); // Enmascaramos el número de tarjeta
                        return payment;
                    })
                    .collect(Collectors.toList()));
        } catch (PaymentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Devolvemos 500 si hay un error
        }
    }
}
