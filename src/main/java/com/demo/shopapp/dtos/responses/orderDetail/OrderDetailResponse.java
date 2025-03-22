package com.demo.shopapp.dtos.responses.orderDetail;

import com.demo.shopapp.entities.OrderDetail;
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

    // ⭐ Thêm phương thức chuyển đổi từ OrderDetail sang OrderDetailResponse
    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .productName(orderDetail.getProduct().getName())
                .productThumbnail(orderDetail.getProduct().getThumbnail()) // Đảm bảo Product có phương thức getThumbnail()
                .unitPrice(orderDetail.getUnitPrice())
                .numberOfProduct(orderDetail.getNumberOfProduct())
                .price(orderDetail.getPrice())
                .color(orderDetail.getColor())
                .build();
    }

}
