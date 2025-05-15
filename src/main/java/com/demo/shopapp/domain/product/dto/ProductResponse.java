package com.demo.shopapp.domain.product.dto;

import com.demo.shopapp.domain.product.entity.Product;
import com.demo.shopapp.domain.product.entity.ProductImage;
import com.demo.shopapp.shared.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse extends BaseResponse {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String thumbnail;

    private String category;

    List<ProductImage> images;

    public static ProductResponse fromProduct(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .category(product.getCategory().getName())
                .images(product.getImages()) // hibernate ganhs
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
