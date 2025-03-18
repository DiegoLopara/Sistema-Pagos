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

import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl; // Clase que estamos probando

    @Mock
    private PaymentService paymentService; // Servicio que se inyecta en la clase a probar

    @Mock
    private PaymentMapper paymentMapper; // Mapper para convertir entre DTO y entidad

    @Mock
    private StreamObserver<PaymentResponse> responseObserver; // Mock para observar las respuestas del gRPC

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks antes de cada prueba
    }

    @Test
    public void testRegisterPaymentSuccess() {
        // Arrange: Preparamos los datos necesarios para la prueba
        PaymentRequest request = createPaymentRequest(); // Creamos un request de pago
        PaymentDTO paymentDTO = createPaymentDTO(); // Creamos un DTO de pago
        Payment payment = createPayment(); // Creamos una entidad de pago
        PaymentResponse expectedResponse = createPaymentResponse(); // Creamos la respuesta esperada

        // Configuramos el comportamiento de los mocks
        when(paymentMapper.toEntity(paymentDTO)).thenReturn(payment); // Mapeamos el DTO a la entidad
        when(paymentService.registerPayment(paymentDTO)).thenReturn(paymentDTO); // Simulamos el registro exitoso
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO); // Mapeamos la entidad de vuelta al DTO
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO); // Aseguramos que el mapeo funcione

        // Act: Llamamos al método que estamos probando
        paymentServiceImpl.registerPayment(request, responseObserver);

        // Assert: Verificamos que se llamaron los métodos esperados en el observer
        verify(responseObserver).onNext(expectedResponse); // Verificamos que se envió la respuesta esperada
        verify(responseObserver).onCompleted(); // Verificamos que se completó la llamada
    }

    @Test
    public void testRegisterPaymentPaymentException() {
        // Preparamos el request de pago
        PaymentRequest request = createPaymentRequest();
        PaymentException exception = new PaymentException("Prefijo de tarjeta inválido."); // Creamos una excepción específica
        doThrow(exception).when(paymentService).registerPayment(any()); // Simulamos que se lanza la excepción al registrar

        // Act: Llamamos al método que estamos probando
        paymentServiceImpl.registerPayment(request, responseObserver);

        // Assert: Verificamos que se envió un error al observer
        verify(responseObserver).onError(argThat(e ->
                e instanceof StatusRuntimeException && // Verificamos que sea una StatusRuntimeException
                        ((StatusRuntimeException) e).getStatus().getCode() == Status.INVALID_ARGUMENT.getCode() && // Verificamos el código de estado
                        ((StatusRuntimeException) e).getStatus().getDescription().equals("Prefijo de tarjeta inválido.")) // Verificamos la descripción
        );
    }

    @Test
    public void testRegisterPaymentGeneralException() {
        // Preparamos el request de pago
        PaymentRequest request = createPaymentRequest();
        when(paymentService.registerPayment(any())).thenThrow(new RuntimeException("General Exception")); // Simulamos una excepción general

        // Act: Llamamos al método que estamos probando
        paymentServiceImpl.registerPayment(request, responseObserver);

        // Assert: Verificamos que se envió un error al observer
        verify(responseObserver).onError(argThat(e ->
                e instanceof StatusRuntimeException && // Verificamos que sea una StatusRuntimeException
                        ((StatusRuntimeException) e).getStatus().getCode() == Status.INTERNAL.getCode() && // Verificamos el código de estado
                        ((StatusRuntimeException) e).getStatus().getDescription().equals("Error al registrar el pago.")) // Verificamos la descripción
        );
    }

    @Test
    public void testGetAllPaymentsSuccess() {
        // Preparamos el request vacío
        Empty request = Empty.newBuilder().build();
        List<PaymentDTO> paymentDTOs = Arrays.asList(createPaymentDTO(), createPaymentDTO()); // Creamos una lista de DTOs de pago

        // Configuramos el comportamiento del mock
        when(paymentService.getAllPayments()).thenReturn(paymentDTOs); // Simulamos la recuperación de pagos

        // Act: Llamamos al método que estamos probando
        paymentServiceImpl.getAllPayments(request, responseObserver);

        // Assert: Verificamos que se enviaron las respuestas esperadas
        verify(responseObserver, times(2)).onNext(any(PaymentResponse.class)); // Verificamos que se llamara dos veces
        verify(responseObserver).onCompleted(); // Verificamos que se completó la llamada
    }

    @Test
    public void testGetAllPaymentsPaymentException() {
        // Preparamos el request vacío
        Empty request = Empty.newBuilder().build();
        when(paymentService.getAllPayments()).thenThrow(new PaymentException("Test Exception")); // Simulamos una excepción

        // Act: Llamamos al método que estamos probando
        paymentServiceImpl.getAllPayments(request, responseObserver);

        // Assert: Verificamos que se envió un error al observer
        verify(responseObserver).onError(argThat(e ->
                e instanceof StatusRuntimeException && // Verificamos que sea una StatusRuntimeException
                        ((StatusRuntimeException) e).getStatus().getCode() == Status.INTERNAL.getCode() && // Verificamos el código de estado
                        ((StatusRuntimeException) e).getStatus().getDescription().equals("Error al obtener los pagos.")) // Verificamos la descripción
        );
    }

    private PaymentRequest createPaymentRequest() {
        // Creamos un objeto Timestamp con la fecha actual
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond())
                .build();

        // Construimos y devolvemos un PaymentRequest con datos de prueba
        return PaymentRequest.newBuilder()
                .setCardNumber("4111111111111111") // Número de tarjeta de prueba
                .setAmount(100.0) // Monto del pago
                .setPaymentDate(timestamp) // Fecha del pago
                .setDescription("Test payment") // Descripción del pago
                .build();
    }

    private PaymentDTO createPaymentDTO() {
        // Creamos un PaymentDTO y le asignamos valores de prueba
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCardNumber("1234567812345678"); // Número de tarjeta de prueba
        paymentDTO.setAmount(BigDecimal.valueOf(100.00)); // Monto del pago
        paymentDTO.setPaymentDate(LocalDate.now()); // Fecha del pago
        paymentDTO.setDescription("Test payment"); // Descripción del pago
        return paymentDTO; // Devolvemos el DTO creado
    }

    private Payment createPayment() {
        // Creamos un objeto Payment y le asignamos valores de prueba
        Payment payment = new Payment();
        payment.setCardNumber("1234567812345678"); // Número de tarjeta de prueba
        payment.setAmount(BigDecimal.valueOf(100.00)); // Monto del pago
        payment.setPaymentDate(LocalDate.now()); // Fecha del pago
        payment.setDescription("Test payment"); // Descripción del pago
        return payment; // Devolvemos la entidad creada
    }

    private PaymentResponse createPaymentResponse() {
        // Creamos un objeto Timestamp con la fecha actual
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond())
                .build();

        // Construimos y devolvemos un PaymentResponse con datos de prueba
        return PaymentResponse.newBuilder()
                .setCardNumber("XXXXXXXXXXXX5678") // Número de tarjeta enmascarado
                .setAmount(100.0) // Monto del pago
                .setPaymentDate(timestamp) // Fecha del pago
                .setDescription("Test payment") // Descripción del pago
                .build(); // Devolvemos la respuesta construida
    }
}
