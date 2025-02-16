package com.demo.shopapp.responses.orderDetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;
//    @JsonProperty("order_id")
//    private Long orderId;
    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_thumbnail")
    private String productThumbnail;

    @JsonProperty("unit_price")
    private double unitPrice;

    @JsonProperty("number_of_product")
    private int numberOfProduct;

    @JsonProperty("price")
    private double price;        // Tính toán dựa trên dữ liệu

    @JsonProperty("color")
    private String color;
}
