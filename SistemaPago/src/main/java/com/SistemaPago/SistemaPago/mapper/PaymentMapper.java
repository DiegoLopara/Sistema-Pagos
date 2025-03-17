package com.SistemaPago.SistemaPago.mapper;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.model.Payment;
import org.springframework.stereotype.Component;

@Component
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
}
