package com.SistemaPago.SistemaPago.server;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.grpc.Empty;
import com.SistemaPago.SistemaPago.grpc.PaymentRequest;
import com.SistemaPago.SistemaPago.grpc.PaymentResponse;
import com.SistemaPago.SistemaPago.grpc.PaymentServiceGrpcGrpc;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.service.PaymentService;
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
public class PaymentServiceGRPCServer extends PaymentServiceGrpcGrpc.PaymentServiceGrpcImplBase{

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public void registerPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        try {
            // Convertir la solicitud a un DTO de pago
            PaymentDTO paymentDTO = requestToPaymentDTO(request);
            System.out.println("PaymentDTO: " + paymentDTO);

            // Registrar el pago utilizando el servicio
            PaymentDTO savedPayment = paymentService.registerPayment(paymentDTO);

            // Enviar la respuesta al observador
            responseObserver.onNext(paymentDtoToPaymentResponse(savedPayment));
            responseObserver.onCompleted();
        } catch (PaymentException e) {
            // Manejar excepciones específicas de pago
            System.err.println("PaymentException al registrar el pago: " + e.getMessage());
            e.printStackTrace();
            responseObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage())));
        } catch (Exception e) {
            // Manejar cualquier otra excepción
            System.err.println("Error general al registrar el pago: " + e.getMessage());
            e.printStackTrace();
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Error al registrar el pago.")));
        }
    }

    @Override
    public void getAllPayments(Empty request, StreamObserver<PaymentResponse> responseObserver) {
        try {
            // Obtener todos los pagos
            List<PaymentDTO> paymentDTOs = paymentService.getAllPayments();
            paymentDTOs.forEach(paymentDTO -> responseObserver.onNext(paymentDtoToPaymentResponse(paymentDTO)));
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Manejar excepciones al recuperar pagos
            System.err.println("Error al recuperar todos los pagos: " + e.getMessage());
            e.printStackTrace();
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Error al recuperar todos los pagos.")));
        }
    }

    private PaymentDTO requestToPaymentDTO(PaymentRequest request) {
        System.out.println("PaymentRequest recibido: " + request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCardNumber(request.getCardNumber());
        paymentDTO.setAmount(BigDecimal.valueOf(request.getAmount()));
        Instant instant = Instant.ofEpochSecond(request.getPaymentDate().getSeconds(), request.getPaymentDate().getNanos());
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        paymentDTO.setPaymentDate(localDate);
        paymentDTO.setDescription(request.getDescription());
        System.out.println("PaymentDTO creado: " + paymentDTO);
        return paymentDTO;
    }

    private PaymentResponse paymentDtoToPaymentResponse(PaymentDTO paymentDTO) {
        Instant instant = paymentDTO.getPaymentDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();

        return PaymentResponse.newBuilder()
                .setCardNumber(paymentDTO.getMaskedCardNumber())
                .setAmount(paymentDTO.getAmount().doubleValue())
                .setPaymentDate(timestamp)
                .setDescription(paymentDTO.getDescription())
                .build();
    }
}
