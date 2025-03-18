package com.SistemaPago.SistemaPago.validations;

import com.SistemaPago.SistemaPago.exception.PaymentException;

public class CardValidator {

    public static void validateCardNumber(String cardNumber) {
        if (!isValidLuhn(cardNumber)) {
            throw new PaymentException("Número de tarjeta inválido.");
        }
        if (!isValidCardLength(cardNumber)) {
            throw new PaymentException("Longitud de tarjeta inválida.");
        }
        if (!isValidCardPrefix(cardNumber)) {
            throw new PaymentException("Prefijo de tarjeta inválido.");
        }
    }

    private static boolean isValidLuhn(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty() || !cardNumber.matches("\\d+")) {
            return false; // No es válido si es nulo, vacío o no contiene solo dígitos
        }

        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private static boolean isValidCardLength(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty() || !cardNumber.matches("\\d+")) {
            return false; // No es válido si es nulo, vacío o no contiene solo dígitos
        }

        int length = cardNumber.length();
        return length >= 13 && length <= 19; // Longitud típica de tarjetas de crédito
    }

    private static boolean isValidCardPrefix(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty() || !cardNumber.matches("\\d+")) {
            return false; // No es válido si es nulo, vacío o no contiene solo dígitos
        }

        // Prefijos comunes de tarjetas de crédito
        return cardNumber.startsWith("34") || cardNumber.startsWith("37") // American Express
                || cardNumber.startsWith("4") // Visa
                || cardNumber.startsWith("51") || cardNumber.startsWith("52") || cardNumber.startsWith("53") || cardNumber.startsWith("54") || cardNumber.startsWith("55") // MasterCard
                || cardNumber.startsWith("6011"); // Discover
    }
}
