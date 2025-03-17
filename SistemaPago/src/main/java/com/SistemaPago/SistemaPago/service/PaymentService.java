package com.SistemaPago.SistemaPago.service;

import com.SistemaPago.SistemaPago.dto.PaymentDTO;
import com.SistemaPago.SistemaPago.exception.PaymentException;
import com.SistemaPago.SistemaPago.mapper.PaymentMapper;
import com.SistemaPago.SistemaPago.model.Payment;
import com.SistemaPago.SistemaPago.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // Marcamos esta clase como servicio
public class PaymentService {

    @Autowired // Inyectamos el repositorio
    private PaymentRepository paymentRepository;

    @Autowired // Inyectamos el mapper
    private PaymentMapper paymentMapper;

    /**
     * Registra un nuevo pago en el sistema.
     *
     * @param paymentDTO DTO que contiene los datos del pago a registrar.
     * @return DTO pago registrado.
     * @throws PaymentException Si ocurre algún error durante el registro.
     */
    public PaymentDTO registerPayment(PaymentDTO paymentDTO) {
        try {
            Payment payment = paymentMapper.toEntity(paymentDTO); // Convierte el DTO a una entidad
            Payment savedPayment = paymentRepository.save(payment); // Guarda la entidad en la base de datos
            return paymentMapper.toDTO(savedPayment); // Convierte la entidad guardada a un DTO y lo devuelve
        } catch (Exception e) {
            throw new PaymentException("Error al registrar el pago", e); // Lanza una excepción personalizada si hay un error
        }
    }


    /**
     * Obtiene una lista de todos los pagos registrados en el sistema.
     *
     * @return Lista de DTOs de todos los pagos.
     * @throws PaymentException Si ocurre algún error durante la recuperación de los pagos.
     */
    public List<PaymentDTO> getAllPayments() {
        try {
            return paymentRepository.findAll().stream() // Obtiene todos los pagos de la base de datos
                    .map(paymentMapper::toDTO) // Convierte cada entidad a un DTO
                    .collect(Collectors.toList()); // Recolecta los DTOs en una lista
        } catch (Exception e) {
            throw new PaymentException("\n" +
                    "Error al recuperar todos los pagos", e); // Lanza una excepción personalizada si hay un error
        }
    }
}
