package com.demo.shopapp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @NotBlank(message = "title is requred")
    @Size(min = 3, max= 200, message = "title must be between 3 and 200 charactors")
    private String name;

    @Min(value = 0, message = "price must be greater than or equal to O")
    @Max(value = 10000000, message = "price must be less than or equal to 10,000,000")
    private Float price;
    private String thumbnail;
    private String description;

    @Min(value = 0, message = "discount must be greater than or equal to O")
    @Max(value = 100, message = "discount must be less than or equal to 100")
    private Integer discount;
    @JsonProperty("category_id")
    private Long categoryId;

    @NotNull(message = "color id is required")
    @JsonProperty("color_id")
    private Long colorId;
}
