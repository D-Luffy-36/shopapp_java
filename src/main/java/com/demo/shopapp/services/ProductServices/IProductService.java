package com.demo.shopapp.services.ProductServices;

import com.demo.shopapp.dtos.ProductDTO;
import com.demo.shopapp.dtos.ProductImageDTO;

import com.demo.shopapp.entities.Product;
import com.demo.shopapp.entities.ProductImage;
import com.demo.shopapp.responses.ListProductResponses;
import com.demo.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Page<Product> getAllProducts(int page, int limit);

    Product getProductById(Long id) throws Exception;
    Product updateProduct(Long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Long id) throws Exception;
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO);

}

