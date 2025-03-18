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
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> registerPayment(@RequestBody PaymentDTO paymentDTO) {
        PaymentDTO savedPayment = paymentService.registerPayment(paymentDTO);
        return ResponseEntity.ok(savedPayment);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
