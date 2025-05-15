package com.demo.shopapp.domain.payment.service;

import com.demo.shopapp.domain.payment.dto.request.PaymentRequestDTO;
import com.demo.shopapp.domain.payment.entity.Payment;

import java.util.Optional;

public class PaymentServiceImp implements  IPaymentService {


    @Override
    public Payment createPayment(PaymentRequestDTO dto) {
        return null;
    }

    @Override
    public Optional<Payment> getPayment(Long id) {
        return Optional.empty();
    }


}
