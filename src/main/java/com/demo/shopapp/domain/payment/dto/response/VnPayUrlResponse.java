package com.demo.shopapp.domain.payment.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Wrapper cho URL thanh toán VNPay.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VnPayUrlResponse {
    /**
     * URL để client chuyển hướng sang cổng VNPay.
     */
    private String payment_url;

    /**
     * VNPayRequest là DTO dùng để nhận dữ liệu từ client
     * khi người dùng gửi yêu cầu thanh toán qua VNPay.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class VNPayRequest {

        @NotNull
        @JsonProperty("order_id")
        private Long orderId; // Thêm orderId để xác định đơn hàng

        @NotNull
        private BigDecimal amount;

        @JsonProperty("order_infor")
        private String orderInfo;

        @JsonProperty("client_email")
        private String clientEmail;

        @JsonProperty("client_phone")
        private String clientPhone;
    }
}
