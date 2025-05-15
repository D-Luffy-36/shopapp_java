package com.demo.shopapp.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {

    @JsonProperty("order_id")
    @Min(value = 1, message = "order_detail id >= 1")
    @NotNull
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "product id >= 1")
    @NotNull
    private Long productId;

    @JsonProperty("number_of_product")
    @Min(value = 1, message = "number product >= 1")
    @NotNull(message = "number_of_product not null")
    private Integer numberOfProduct;

    private String color;

    @JsonProperty("unit_price")
    @Min(value = 0, message = "unit_price >= 0")
    @NotNull(message = "unit_price not null")
    private Float unitPrice;

    @JsonProperty("price")
    @Min(value = 0, message = "price >= 0")
    @NotNull(message = "unit_price not null")
    private Float price;
}
