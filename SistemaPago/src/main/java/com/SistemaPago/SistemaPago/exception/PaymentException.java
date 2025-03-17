package com.SistemaPago.SistemaPago.exception;

public class PaymentException extends RuntimeException{ // Nuestra excepción personalizada, hereda de RuntimeException

    public PaymentException(String message) { // Constructor con mensaje
        super(message);
    }

    public PaymentException(String message, Throwable cause) {  // Constructor con mensaje y causa
        super(message, cause);
    }
}
