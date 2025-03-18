package com.SistemaPago.SistemaPago.controller;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.grpc.PaymentListResponse;
import com.SistemaPago.SistemaPago.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PaymentListResponse> getAllPayments() {
        PaymentListResponse payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
