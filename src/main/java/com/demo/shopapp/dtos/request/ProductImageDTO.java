package com.demo.shopapp.dtos.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductImageDTO {

    @JsonProperty("product_id")
    private Long productId;

    @Size(min = 5, max = 200, message = "image name beetween 5 and 200 charector ")
    private String imageUrl;


}
