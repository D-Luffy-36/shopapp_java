package com.demo.shopapp.domain.payment.service;

import com.demo.shopapp.domain.payment.dto.request.PaymentRequestDTO;
import com.demo.shopapp.domain.payment.entity.Payment;

import java.util.Optional;

public interface IPaymentService {
    Payment createPayment(PaymentRequestDTO dto);
    Optional<Payment> getPayment(Long id);
}
