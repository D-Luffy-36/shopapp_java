package com.demo.shopapp.domain.payment.dto.request;

import com.demo.shopapp.domain.payment.entity.Payment;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;


@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentRequestDTO {
    private Long orderId;
    private Long userId;
    private String paymentMethod;
    private BigDecimal amount;
    private Payment.PaymentStatus status;
}
