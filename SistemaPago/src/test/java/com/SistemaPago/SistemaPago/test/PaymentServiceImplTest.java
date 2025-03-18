package com.SistemaPago.SistemaPago.test;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.grpc.Empty;
import com.SistemaPago.SistemaPago.grpc.PaymentListResponse;
import com.SistemaPago.SistemaPago.grpc.PaymentRequest;
import com.SistemaPago.SistemaPago.grpc.PaymentResponse;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.server.PaymentServiceGRPCServer;
import com.SistemaPago.SistemaPago.service.PaymentService;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceGRPCServer paymentServiceGRPCServer;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private StreamObserver<PaymentListResponse> responseObserverList; // Cambiado a PaymentListResponse

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

        when(paymentMapper.toDTO(request)).thenReturn(paymentDTO);
        when(paymentService.registerPayment(paymentDTO)).thenReturn(paymentDTO);
        when(paymentMapper.toResponse(paymentDTO)).thenReturn(expectedResponse);

        paymentServiceGRPCServer.registerPayment(request, responseObserver);

        verify(responseObserver).onNext(expectedResponse);
        verify(responseObserver).onCompleted();
    }

    @Test
    public void testRegisterPaymentPaymentException() {
        PaymentRequest request = createPaymentRequest();
        PaymentDTO paymentDTO = createPaymentDTO();
        PaymentException exception = new PaymentException("Prefijo de tarjeta inválido.");
        when(paymentMapper.toDTO(request)).thenReturn(paymentDTO);
        doThrow(exception).when(paymentService).registerPayment(paymentDTO);

        paymentServiceGRPCServer.registerPayment(request, responseObserver);

        verify(responseObserver).onError(argThat(e ->
                e instanceof StatusRuntimeException &&
                        ((StatusRuntimeException) e).getStatus().getCode() == Status.INVALID_ARGUMENT.getCode() &&
                        ((StatusRuntimeException) e).getStatus().getDescription().equals("Prefijo de tarjeta inválido."))
        );
    }

    @Test
    public void testRegisterPaymentGeneralException() {
        PaymentRequest request = createPaymentRequest();
        PaymentDTO paymentDTO = createPaymentDTO();
        when(paymentMapper.toDTO(request)).thenReturn(paymentDTO);
        when(paymentService.registerPayment(paymentDTO)).thenThrow(new RuntimeException("General Exception"));

        paymentServiceGRPCServer.registerPayment(request, responseObserver);

        verify(responseObserver).onError(argThat(e ->
                e instanceof StatusRuntimeException &&
                        ((StatusRuntimeException) e).getStatus().getCode() == Status.INTERNAL.getCode() &&
                        ((StatusRuntimeException) e).getStatus().getDescription().equals("Error al registrar el pago."))
        );
    }

    @Test
    public void testGetAllPaymentsSuccess() {
        Empty request = Empty.newBuilder().build();
        List<PaymentDTO> paymentDTOs = Arrays.asList(createPaymentDTO(), createPaymentDTO());
        List<PaymentResponse> paymentResponses = paymentDTOs.stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        PaymentListResponse expectedResponse = PaymentListResponse.newBuilder()
                .addAllPayments(paymentResponses)
                .build();

        when(paymentService.getAllPayments()).thenReturn(expectedResponse);

        paymentServiceGRPCServer.getAllPayments(request, responseObserverList);

        verify(responseObserverList).onNext(expectedResponse);
        verify(responseObserverList).onCompleted();
    }

    @Test
    public void testGetAllPaymentsPaymentException() {
        Empty request = Empty.newBuilder().build();
        when(paymentService.getAllPayments()).thenThrow(new PaymentException("Test Exception"));

        paymentServiceGRPCServer.getAllPayments(request, responseObserverList);

        verify(responseObserverList).onError(argThat(e ->
                e instanceof StatusRuntimeException &&
                        ((StatusRuntimeException) e).getStatus().getCode() == Status.INTERNAL.getCode() &&
                        ((StatusRuntimeException) e).getStatus().getDescription().equals("Error al obtener los pagos."))
        );
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
                .setCardNumber("XXXXXXXXXXXX5678")
                .setAmount(100.0)
                .setPaymentDate(timestamp)
                .setDescription("Test payment")
                .build();
    }
}
