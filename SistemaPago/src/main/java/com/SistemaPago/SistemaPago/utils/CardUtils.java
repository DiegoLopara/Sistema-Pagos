package com.SistemaPago.SistemaPago.utils;

public class CardUtils {

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() <= 4) {
            return cardNumber; // Retorna el número original si es nulo o tiene 4 o menos dígitos
        }
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < cardNumber.length() - 4; i++) {
            maskedNumber.append("X"); // Agrega 'X' para cada dígito enmascarado
        }
        return maskedNumber.toString() + lastFourDigits; // Combina el número enmascarado con los últimos cuatro dígitos
    }
}
