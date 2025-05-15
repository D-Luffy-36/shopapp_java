package com.demo.shopapp.domain.payment.repository;

import com.demo.shopapp.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}