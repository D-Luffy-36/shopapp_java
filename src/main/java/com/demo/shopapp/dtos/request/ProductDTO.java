package com.demo.shopapp.dtos.request;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @NotBlank(message = "name is required")
    @Size(min = 3, max = 200, message = "title must be between 3 and 200 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    @Min(value = 0, message = "price must be greater than or equal to O")
    @Max(value = 10000000, message = "price must be less than or equal to 10,000,000")
    private Float price;
    private String thumbnail;
    @JsonProperty("description")
    private String description;

    @Min(value = 0, message = "discount must be greater than or equal to O")
    @Max(value = 100, message = "discount must be less than or equal to 100")
    @JsonProperty("discount")
    private Float discount;

    @JsonProperty("category_id")
    private Long categoryId;

}
