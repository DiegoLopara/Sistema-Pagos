package com.SistemaPago.SistemaPago.test;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.repository.PaymentRepository;
import com.SistemaPago.SistemaPago.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    public PaymentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterPayment() {
        // Crear un PaymentDTO para la prueba
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCardNumber("1234567812345678");
        paymentDTO.setAmount(BigDecimal.valueOf(100.00));
        paymentDTO.setPaymentDate(LocalDate.now());
        paymentDTO.setDescription("Test payment");

        // Simular el comportamiento del mapper y el repositorio
        Payment payment = new Payment();
        payment.setCardNumber(paymentDTO.getCardNumber());
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setDescription(paymentDTO.getDescription());

        when(paymentMapper.toEntity(paymentDTO)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

        // Ejecutar el método a probar
        PaymentDTO createdPayment = paymentService.registerPayment(paymentDTO);

        // Verificar los resultados
        assertEquals("1234567812345678", createdPayment.getCardNumber());
        assertEquals(BigDecimal.valueOf(100.00), createdPayment.getAmount());
    }
}
