package com.demo.shopapp.dtos.responses.product;

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
