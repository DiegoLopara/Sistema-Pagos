package com.SistemaPago.SistemaPago.model;

import com.SistemaPago.SistemaPago.utils.CardUtils;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity // Indica que esta clase es una entidad JPA
@Table(name = "payments") // Especifica el nombre de la tabla
public class Payment {
    @Id // Indica que este campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Genera automáticamente el ID
    private Long id;

    @Column(name = "card_number", nullable = false) // Columna para el número de tarjeta
    private String cardNumber;

    @Column(name = "amount", nullable = false) // Columna para la cantidad de dinero
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false) // Columna para la fecha
    private LocalDate paymentDate;

    @Column(name = "description") // Columna para la descripción
    private String description;

    public Payment(Long id, String cardNumber, BigDecimal amount, LocalDate paymentDate, String description) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.description = description;
    }

    public Payment() {
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
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
