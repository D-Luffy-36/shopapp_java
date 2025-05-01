package com.demo.shopapp.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @NotBlank(message = "category's name cannot be blank")
    private String name;

    public @NotBlank(message = "category's name cannot be blank") String getName() {
        return name;
    }
}
