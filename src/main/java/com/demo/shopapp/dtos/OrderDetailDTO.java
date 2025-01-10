package com.demo.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
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
    @NotNull
    private Integer numberOfProduct;

    private String color;

    @JsonProperty("price")
    @Min(value = 0, message = "price >= 0")
    @NotNull(message = "price not null")
    private Float price;

    @JsonProperty("total_money")
    @Min(value = 0, message = "totall >= 0")
    @NotNull(message = "total money >= 0")
    private Float totalMoney;
}

//
//CREATE TABLE [order_details] (
//        [id] BIGINT PRIMARY KEY IDENTITY(1, 1),
//        [order_id] BIGINT,
//        [product_id] BIGINT,
//        [price] float,
//        [number_of_product] int,
//        [total_money] float,
//        [color] varchar(20) DEFAULT ''
//        )
//GO
