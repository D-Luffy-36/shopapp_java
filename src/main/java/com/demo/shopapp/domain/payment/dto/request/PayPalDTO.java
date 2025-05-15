package com.demo.shopapp.domain.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * PayPalDTO là DTO dùng để nhận dữ liệu từ client
 * khi người dùng gửi yêu cầu thanh toán qua PayPal.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayPalDTO {
    private BigDecimal amount;
    private String currency;
    private String description;
    @JsonProperty("order_id")
    private Long orderId;
}
