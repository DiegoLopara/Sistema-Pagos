package com.SistemaPago.SistemaPago.mapper;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.grpc.PaymentRequest;
import com.SistemaPago.SistemaPago.grpc.PaymentResponse;
import com.SistemaPago.SistemaPago.model.Payment;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component // Marcamos esta clase como un componente de Spring
public class PaymentMapper {

    public Payment toEntity(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setCardNumber(paymentDTO.getCardNumber());
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setDescription(paymentDTO.getDescription());
        return payment;
    }

    public PaymentDTO toDTO(Payment payment) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCardNumber(payment.getCardNumber());
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setPaymentDate(payment.getPaymentDate());
        paymentDTO.setDescription(payment.getDescription());
        return paymentDTO;
    }

    public PaymentDTO toDTO(PaymentRequest request) {
        System.out.println("PaymentRequest recibido: " + request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCardNumber(request.getCardNumber());
        paymentDTO.setAmount(BigDecimal.valueOf(request.getAmount()));
        Instant instant = Instant.ofEpochSecond(request.getPaymentDate().getSeconds(), request.getPaymentDate().getNanos());
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        paymentDTO.setPaymentDate(localDate);
        paymentDTO.setDescription(request.getDescription());
        System.out.println("PaymentDTO creado: " + paymentDTO);
        return paymentDTO;
    }

    public PaymentResponse toResponse(PaymentDTO paymentDTO) {
        Instant instant = paymentDTO.getPaymentDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();

        return PaymentResponse.newBuilder()
                .setCardNumber(paymentDTO.getMaskedCardNumber())
                .setAmount(paymentDTO.getAmount().doubleValue())
                .setPaymentDate(timestamp)
                .setDescription(paymentDTO.getDescription())
                .build();
    }
}
