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
    private PaymentService paymentService;

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public void registerPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        try {
            Validations.validatePaymentRequest(request);
            CardValidator.validateCardNumber(request.getCardNumber());

            PaymentDTO paymentDTO = requestToPaymentDTO(request);
            Payment payment = paymentMapper.toEntity(paymentDTO);
            PaymentDTO savedPayment = paymentService.registerPayment(paymentMapper.toDTO(payment));

            responseObserver.onNext(paymentDtoToPaymentResponse(savedPayment));
            responseObserver.onCompleted();
        } catch (PaymentException e) {
            responseObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage())));
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Error al registrar el pago.")));
        }
    }
    @Override
    public void getAllPayments(Empty request, StreamObserver<PaymentResponse> responseObserver) {
        try {
            List<PaymentDTO> paymentDTOs = paymentService.getAllPayments();
            paymentDTOs.forEach(paymentDTO -> responseObserver.onNext(paymentDtoToPaymentResponse(paymentDTO)));
            responseObserver.onCompleted();
        } catch (PaymentException e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Error al obtener los pagos.")));
        }
    }

    private PaymentDTO requestToPaymentDTO(PaymentRequest request) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCardNumber(request.getCardNumber());
        paymentDTO.setAmount(BigDecimal.valueOf(request.getAmount()));
        Instant instant = Instant.ofEpochSecond(request.getPaymentDate().getSeconds(), request.getPaymentDate().getNanos());
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        paymentDTO.setPaymentDate(localDate);
        paymentDTO.setDescription(request.getDescription());
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
