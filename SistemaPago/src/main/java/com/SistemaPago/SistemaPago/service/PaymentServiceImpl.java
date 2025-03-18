package com.SistemaPago.SistemaPago.service;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.grpc.Empty;
import com.SistemaPago.SistemaPago.grpc.PaymentRequest;
import com.SistemaPago.SistemaPago.grpc.PaymentResponse;
import com.SistemaPago.SistemaPago.grpc.PaymentServiceGrpcGrpc;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.validations.CardValidator;
import com.SistemaPago.SistemaPago.validations.Validations;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@GrpcService
public class PaymentServiceImpl extends PaymentServiceGrpcGrpc.PaymentServiceGrpcImplBase {

    @Autowired
    private PaymentService paymentService; // Servicio que maneja la lógica de negocio para pagos

    @Autowired
    private PaymentMapper paymentMapper; // Mapper que convierte entre DTO y entidad

    @Override
    public void registerPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        try {
            // Validamos la solicitud de pago
            Validations.validatePaymentRequest(request);
            // Validamos el número de tarjeta
            CardValidator.validateCardNumber(request.getCardNumber());

            // Convertimos la solicitud a un DTO de pago
            PaymentDTO paymentDTO = requestToPaymentDTO(request);
            // Convertimos el DTO a una entidad
            Payment payment = paymentMapper.toEntity(paymentDTO);
            // Registramos el pago y obtenemos el DTO guardado
            PaymentDTO savedPayment = paymentService.registerPayment(paymentMapper.toDTO(payment));

            // Enviamos la respuesta al observador
            responseObserver.onNext(paymentDtoToPaymentResponse(savedPayment));
            // Indicamos que hemos terminado de enviar respuestas
            responseObserver.onCompleted();
        } catch (PaymentException e) {
            // En caso de una excepción de pago, enviamos un error al observador
            responseObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage())));
        } catch (Exception e) {
            // En caso de cualquier otra excepción, enviamos un error interno
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Error al registrar el pago.")));
        }
    }

    @Override
    public void getAllPayments(Empty request, StreamObserver<PaymentResponse> responseObserver) {
        try {
            // Obtenemos todos los pagos como una lista de DTOs
            List<PaymentDTO> paymentDTOs = paymentService.getAllPayments();
            // Enviamos cada DTO como respuesta al observador
            paymentDTOs.forEach(paymentDTO -> responseObserver.onNext(paymentDtoToPaymentResponse(paymentDTO)));
            // Indicamos que hemos terminado de enviar respuestas
            responseObserver.onCompleted();
        } catch (PaymentException e) {
            // En caso de una excepción de pago, enviamos un error interno
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Error al obtener los pagos.")));
        }
    }

    private PaymentDTO requestToPaymentDTO(PaymentRequest request) {
        // Creamos un nuevo DTO de pago
        PaymentDTO paymentDTO = new PaymentDTO();
        // Asignamos el número de tarjeta desde la solicitud
        paymentDTO.setCardNumber(request.getCardNumber());
        // Asignamos el monto desde la solicitud
        paymentDTO.setAmount(BigDecimal.valueOf(request.getAmount()));
        // Convertimos la fecha de pago desde el formato de Protobuf a LocalDate
        Instant instant = Instant.ofEpochSecond(request.getPaymentDate().getSeconds(), request.getPaymentDate().getNanos());
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        paymentDTO.setPaymentDate(localDate); // Asignamos la fecha de pago
        // Asignamos la descripción desde la solicitud
        paymentDTO.setDescription(request.getDescription());
        return paymentDTO; // Devolvemos el DTO creado
    }

    private PaymentResponse paymentDtoToPaymentResponse(PaymentDTO paymentDTO) {
        // Convertimos la fecha de pago a un formato de Protobuf
        Instant instant = paymentDTO.getPaymentDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();

        // Construimos y devolvemos la respuesta de pago
        return PaymentResponse.newBuilder()
                .setCardNumber(paymentDTO.getMaskedCardNumber()) // Asignamos el número de tarjeta enmascarado
                .setAmount(paymentDTO.getAmount().doubleValue()) // Asignamos el monto del pago
                .setPaymentDate(timestamp) // Asignamos la fecha de pago
                .setDescription(paymentDTO.getDescription()) // Asignamos la descripción del pago
                .build(); // Devolvemos la respuesta construida
    }
}
