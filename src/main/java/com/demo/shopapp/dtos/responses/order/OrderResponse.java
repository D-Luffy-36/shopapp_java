package com.demo.shopapp.dtos.responses.order;

import com.demo.shopapp.entities.Order;
import com.demo.shopapp.dtos.responses.BaseResponse;
import com.demo.shopapp.dtos.responses.orderDetail.OrderDetailResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class OrderResponse extends BaseResponse {
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("full_name")
    private String fullName;

    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    private String status;

    @JsonProperty("total_money")
    private Double totalMoney;
    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("payment_method")
    private String paymentMethod;
//    @JsonProperty("shipping_address")
//    private String shippingAddress;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonProperty("shipping_date")
    private LocalDateTime shippingDate;
    @JsonProperty("tracking_number")
    private String trackingNumber;

    private double discount;

    private Boolean active;

    @JsonProperty("order_details")
    List<OrderDetailResponse> orderDetailResponses;

    public static OrderResponse fromOrder(Order order) {
        OrderResponse orderResponse = OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullName(order.getFullName())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
//                .shippingAddress(order.getShippingAddress())
                .shippingDate(order.getShippingDate())
                .trackingNumber(order.getTrackingNumber())
                .discount(order.getDiscount())
                .active(order.getActive())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderDetailResponses(
                        order.getOrderDetails().stream()
                                .map(OrderDetailResponse::fromOrderDetail) // Chuyển đổi sang DTO
                                .collect(Collectors.toList())
                )
                .build();
        return orderResponse;
    }
}
