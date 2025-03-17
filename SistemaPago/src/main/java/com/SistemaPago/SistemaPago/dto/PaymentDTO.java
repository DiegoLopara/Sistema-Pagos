package com.SistemaPago.SistemaPago.dto;

import com.SistemaPago.SistemaPago.utils.CardUtils;

import java.math.BigDecimal;
import java.time.LocalDate;


public class PaymentDTO {
    private String cardNumber;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String description;

    public PaymentDTO(String cardNumber, BigDecimal amount, LocalDate paymentDate, String description) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.description = description;
    }

    public PaymentDTO() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaskedCardNumber() {
        return CardUtils.maskCardNumber(cardNumber);
    }
}
