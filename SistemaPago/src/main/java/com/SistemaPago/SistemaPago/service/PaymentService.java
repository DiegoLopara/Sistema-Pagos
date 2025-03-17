package com.SistemaPago.SistemaPago.service;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    public PaymentDTO registerPayment(PaymentDTO paymentDTO) {
        try {
            Payment payment = paymentMapper.toEntity(paymentDTO);
            Payment savedPayment = paymentRepository.save(payment);
            return paymentMapper.toDTO(savedPayment);
        } catch (Exception e) {
            throw new PaymentException("Error al registrar el pago", e);
        }
    }

    public List<PaymentDTO> getAllPayments() {
        try {
            return paymentRepository.findAll().stream()
                    .map(paymentMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PaymentException("\n" +
                    "Error al recuperar todos los pagos", e);
        }
    }
}
