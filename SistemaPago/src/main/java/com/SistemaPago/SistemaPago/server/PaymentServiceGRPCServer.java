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
        PaymentDTO paymentDTO = paymentMapper.toDTO(request);
        System.out.println("PaymentDTO: " + paymentDTO);

        PaymentDTO savedPayment = paymentService.registerPayment(paymentDTO);

        responseObserver.onNext(paymentMapper.toResponse(savedPayment));
        responseObserver.onCompleted();
    }

    @Override
    public void getAllPayments(Empty request, StreamObserver<PaymentResponse> responseObserver) {
        List<PaymentDTO> paymentDTOs = paymentService.getAllPayments();
        paymentDTOs.forEach(paymentDTO -> responseObserver.onNext(paymentMapper.toResponse(paymentDTO)));
        responseObserver.onCompleted();
    }


}
