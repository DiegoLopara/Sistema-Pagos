package com.SistemaPago.SistemaPago.test;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.grpc.Empty;
import com.SistemaPago.SistemaPago.grpc.PaymentRequest;
import com.SistemaPago.SistemaPago.grpc.PaymentResponse;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.service.PaymentService;
import com.SistemaPago.SistemaPago.service.PaymentServiceImpl;
import com.SistemaPago.SistemaPago.validations.CardValidator;
import com.SistemaPago.SistemaPago.validations.Validations;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private StreamObserver<PaymentResponse> responseObserver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterPaymentSuccess() {
        PaymentRequest request = createPaymentRequest();
        PaymentDTO paymentDTO = createPaymentDTO();
        Payment payment = createPayment();
        PaymentResponse expectedResponse = createPaymentResponse();

        CardValidator.validateCardNumber(request.getCardNumber()); // Validación de tarjeta
        when(paymentMapper.toEntity(paymentDTO)).thenReturn(payment);
        when(paymentService.registerPayment(paymentDTO)).thenReturn(paymentDTO);
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

        paymentServiceImpl.registerPayment(request, responseObserver);

        verify(responseObserver).onNext(expectedResponse);
        verify(responseObserver).onCompleted();
    }

    @Test
    public void testRegisterPaymentFailure() {
        PaymentRequest request = createPaymentRequest();
        when(paymentService.registerPayment(any())).thenThrow(new PaymentException("Test Exception"));

        paymentServiceImpl.registerPayment(request, responseObserver);

        verify(responseObserver).onError(any(PaymentException.class));
    }

    @Test
    public void testGetAllPaymentsSuccess() {
        Empty request = Empty.newBuilder().build();
        List<PaymentDTO> paymentDTOs = Arrays.asList(createPaymentDTO(), createPaymentDTO());
        PaymentResponse response1 = createPaymentResponse();
        PaymentResponse response2 = createPaymentResponse();

        when(paymentService.getAllPayments()).thenReturn(paymentDTOs);

        paymentServiceImpl.getAllPayments(request, responseObserver);

        verify(responseObserver, times(2)).onNext(any(PaymentResponse.class)); // Expect 2 calls
        verify(responseObserver).onCompleted();
    }
    @Test
    public void testGetAllPaymentsFailure() {
        Empty request = Empty.newBuilder().build();
        when(paymentService.getAllPayments()).thenThrow(new PaymentException("Test Exception"));

        paymentServiceImpl.getAllPayments(request, responseObserver);

        verify(responseObserver).onError(any(PaymentException.class));
    }

    private PaymentRequest createPaymentRequest() {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond())
                .build();

        return PaymentRequest.newBuilder()
                .setCardNumber("4111111111111111")
                .setAmount(100.0)
                .setPaymentDate(timestamp)
                .setDescription("Test payment")
                .build();
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

    private PaymentResponse createPaymentResponse() {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond())
                .build();

        return PaymentResponse.newBuilder()
                .setCardNumber("XXXXXXXXXXXX5678") // Use the correct masking
                .setAmount(100.0)
                .setPaymentDate(timestamp)
                .setDescription("Test payment")
                .build();
    }
}
