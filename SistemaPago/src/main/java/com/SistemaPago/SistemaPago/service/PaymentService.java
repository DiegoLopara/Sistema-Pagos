package com.SistemaPago.SistemaPago.service;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.grpc.PaymentListResponse;
import com.SistemaPago.SistemaPago.grpc.PaymentResponse;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.repository.PaymentRepository;
import com.SistemaPago.SistemaPago.validations.CardValidator;
import com.SistemaPago.SistemaPago.validations.Validations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service // Marcamos esta clase como servicio
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);


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

    public PaymentListResponse getAllPayments() {
        try {
            logger.info("Recuperando todos los pagos de la base de datos.");

            List<Payment> payments = StreamSupport.stream(paymentRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());

            List<PaymentDTO> paymentDTOs = payments.stream()
                    .map(paymentMapper::toDTO)
                    .collect(Collectors.toList());

            List<PaymentResponse> paymentResponses = paymentDTOs.stream()
                    .map(paymentMapper::toResponse)
                    .collect(Collectors.toList());

            logger.info("Se recuperaron {} pagos.", paymentResponses.size());

            return PaymentListResponse.newBuilder()
                    .addAllPayments(paymentResponses)
                    .build();
        } catch (DataAccessException e) {
            logger.error("Error al acceder a la base de datos: {}", e.getMessage());
            throw new PaymentException("Error al recuperar todos los pagos", e);
        } catch (Exception e) {
            logger.error("Error general al recuperar todos los pagos: {}", e.getMessage());
            throw new PaymentException("Error al recuperar todos los pagos", e);
        }
    }
}
