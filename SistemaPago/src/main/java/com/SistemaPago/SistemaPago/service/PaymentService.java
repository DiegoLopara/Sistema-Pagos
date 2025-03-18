package com.SistemaPago.SistemaPago.service;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.repository.PaymentRepository;
import com.SistemaPago.SistemaPago.validations.CardValidator;
import com.SistemaPago.SistemaPago.validations.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // Marcamos esta clase como servicio
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    public PaymentDTO registerPayment(PaymentDTO paymentDTO) {
        try {
            Validations.validatePaymentRequest(paymentDTO);
            CardValidator.validateCardNumber(paymentDTO.getCardNumber());
            Payment payment = paymentMapper.toEntity(paymentDTO);
            Payment savedPayment = paymentRepository.save(payment);
            return paymentMapper.toDTO(savedPayment);
        } catch (PaymentException e) {
            System.err.println("PaymentException en PaymentService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("Error general en PaymentService: " + e.getMessage());
            e.printStackTrace();
            throw new PaymentException("Error al registrar el pago", e);
        }
    }

    public List<PaymentDTO> getAllPayments() {
        try {
            return paymentRepository.findAll().stream()
                    .map(paymentMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al recuperar todos los pagos en PaymentService: " + e.getMessage());
            e.printStackTrace();
            throw new PaymentException("Error al recuperar todos los pagos", e);
        }
    }
}
