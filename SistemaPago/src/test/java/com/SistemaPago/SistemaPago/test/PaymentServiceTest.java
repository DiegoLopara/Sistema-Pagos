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
    @InjectMocks // Inyectamos el servicio que vamos a testear
    private PaymentService paymentService;

    @Mock // Creamos un mock para el repositorio
    private PaymentRepository paymentRepository;

    @Mock // Creamos un mock para el mapper
    private PaymentMapper paymentMapper;

    @BeforeEach // Se ejecuta antes de cada test
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializamos los mocks
    }

    @Test // Indica que este método es un test
    public void testRegisterPaymentSuccess() {
        PaymentDTO paymentDTO = createPaymentDTO(); // Creamos un DTO de prueba
        Payment payment = createPayment(); // Creamos una entidad de prueba

        when(paymentMapper.toEntity(paymentDTO)).thenReturn(payment); // Configuramos el comportamiento del mapper
        when(paymentRepository.save(payment)).thenReturn(payment); // Configuramos el comportamiento del repositorio
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO); // Configuramos el comportamiento del mapper

        PaymentDTO createdPayment = paymentService.registerPayment(paymentDTO); // Ejecutamos el método a testear

        assertEquals(paymentDTO, createdPayment); // Verificamos que el resultado sea el esperado
    }

    @Test // Indica que este método es un test
    public void testRegisterPaymentFailure() {
        PaymentDTO paymentDTO = createPaymentDTO(); // Creamos un DTO de prueba

        when(paymentMapper.toEntity(paymentDTO)).thenThrow(new RuntimeException("Mapper Error")); // Configuramos el mapper para lanzar una excepción

        assertThrows(PaymentException.class, () -> paymentService.registerPayment(paymentDTO)); // Verificamos que se lance la excepción esperada
    }

    @Test // Indica que este método es un test
    public void testGetAllPaymentsSuccess() {
        List<Payment> payments = Arrays.asList(createPayment(), createPayment()); // Creamos una lista de entidades de prueba
        List<PaymentDTO> paymentDTOs = Arrays.asList(createPaymentDTO(), createPaymentDTO()); // Creamos una lista de DTOs de prueba

        when(paymentRepository.findAll()).thenReturn(payments); // Configuramos el comportamiento del repositorio
        when(paymentMapper.toDTO(payments.get(0))).thenReturn(paymentDTOs.get(0)); // Configuramos el comportamiento del mapper
        when(paymentMapper.toDTO(payments.get(1))).thenReturn(paymentDTOs.get(1)); // Configuramos el comportamiento del mapper

        List<PaymentDTO> retrievedPayments = paymentService.getAllPayments(); // Ejecutamos el método a testear

        assertEquals(paymentDTOs, retrievedPayments); // Verificamos que el resultado sea el esperado
    }

    @Test // Indica que este método es un test
    public void testGetAllPaymentsFailure() {
        when(paymentRepository.findAll()).thenThrow(new RuntimeException("Database error")); // Configuramos el repositorio para lanzar una excepción

        assertThrows(PaymentException.class, () -> paymentService.getAllPayments()); // Verificamos que se lance la excepción esperada
    }

    private PaymentDTO createPaymentDTO() { // Método para crear un DTO de prueba
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCardNumber("1234567812345678");
        paymentDTO.setAmount(BigDecimal.valueOf(100.00));
        paymentDTO.setPaymentDate(LocalDate.now());
        paymentDTO.setDescription("Test payment");
        return paymentDTO;
    }

    private Payment createPayment() { // Método para crear una entidad de prueba
        Payment payment = new Payment();
        payment.setCardNumber("1234567812345678");
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setPaymentDate(LocalDate.now());
        payment.setDescription("Test payment");
        return payment;
    }
}