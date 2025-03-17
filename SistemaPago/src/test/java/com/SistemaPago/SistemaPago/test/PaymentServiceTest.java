package com.SistemaPago.SistemaPago.test;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.repository.PaymentRepository;
import com.SistemaPago.SistemaPago.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterPaymentSuccess() {
        PaymentDTO paymentDTO = createPaymentDTO();
        Payment payment = createPayment();

        when(paymentMapper.toEntity(paymentDTO)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

        PaymentDTO createdPayment = paymentService.registerPayment(paymentDTO);

        assertEquals(paymentDTO, createdPayment);
    }

    @Test
    public void testRegisterPaymentFailure() {
        PaymentDTO paymentDTO = createPaymentDTO();

        when(paymentMapper.toEntity(paymentDTO)).thenThrow(new RuntimeException("Mapper Error"));

        assertThrows(PaymentException.class, () -> paymentService.registerPayment(paymentDTO));

    }

    @Test
    public void testGetAllPaymentsSuccess() {
        List<Payment> payments = Arrays.asList(createPayment(), createPayment());
        List<PaymentDTO> paymentDTOs = Arrays.asList(createPaymentDTO(), createPaymentDTO());

        when(paymentRepository.findAll()).thenReturn(payments);
        when(paymentMapper.toDTO(payments.get(0))).thenReturn(paymentDTOs.get(0));
        when(paymentMapper.toDTO(payments.get(1))).thenReturn(paymentDTOs.get(1));

        List<PaymentDTO> retrievedPayments = paymentService.getAllPayments();

        assertEquals(paymentDTOs, retrievedPayments);
    }

    @Test
    public void testGetAllPaymentsFailure() {
        when(paymentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(PaymentException.class, () -> paymentService.getAllPayments());
    }

    private PaymentDTO createPaymentDTO() {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCardNumber("1234567812345678");
        paymentDTO.setAmount(BigDecimal.valueOf(100.00));
        paymentDTO.setPaymentDate(LocalDate.now());
        paymentDTO.setDescription("Test payment");
        return paymentDTO;
    }

    private Payment createPayment() {
        Payment payment = new Payment();
        payment.setCardNumber("1234567812345678");
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setPaymentDate(LocalDate.now());
        payment.setDescription("Test payment");
        return payment;
    }
}
