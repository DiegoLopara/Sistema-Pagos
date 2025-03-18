package com.SistemaPago.SistemaPago.validations;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.grpc.PaymentRequest;

public class Validations {

    public static void validatePayment(PaymentRequest request) {
        if (request.getCardNumber() == null || request.getCardNumber().isEmpty()) {
            throw new PaymentException("El número de tarjeta es obligatorio.");
        }
        if (request.getAmount() <= 0) {
            throw new PaymentException("El monto debe ser mayor que cero.");
        }
        if (request.getPaymentDate() == null) {
            throw new PaymentException("La fecha de pago es obligatoria.");
        }
    }

    public static void validatePaymentRequest(PaymentDTO dto) {
        if (dto.getCardNumber() == null || dto.getCardNumber().isEmpty()) {
            throw new PaymentException("El número de tarjeta es obligatorio.");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new PaymentException("El monto debe ser mayor que cero.");
        }
        if (dto.getPaymentDate() == null) {
            throw new PaymentException("La fecha de pago es obligatoria.");
        }
    }
}
