package com.demo.shopapp.responses.orderDetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;// Thêm ID từ database
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("product_name")
    private String productName;

    private float price;
    @JsonProperty("number_of_product")
    private int numberOfProduct;

    @JsonProperty("total_money")
    private float totalMoney;        // Tính toán dựa trên dữ liệu
    private String color;
}
