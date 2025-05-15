package com.demo.shopapp.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListProductResponse {
    private List<ProductResponse> products;
    private int totalPages;
}
