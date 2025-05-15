package com.demo.shopapp.domain.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayPalResponse {

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("is_success")
    private Boolean isSuccess;

    @JsonProperty("payer_id")
    private String payerId;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("error")
    private String error;
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("order_status")
    private String orderStatus;   // Thêm trường này
}
