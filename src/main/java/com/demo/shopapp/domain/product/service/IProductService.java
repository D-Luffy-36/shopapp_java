package com.demo.shopapp.domain.product.service;

import com.demo.shopapp.domain.product.dto.ProductDTO;
import com.demo.shopapp.domain.product.dto.ProductImageDTO;

import com.demo.shopapp.domain.product.entity.Product;
import com.demo.shopapp.entities.ProductImage;
import com.demo.shopapp.shared.exceptions.InvalidParamException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Page<Product> getAllProducts(String keyWord, Long category_id ,int page, int limit);

    Product getProductById(Long id) throws Exception;
    Product updateProduct(Long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Long id) throws Exception;
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws InvalidParamException;

    List<ProductImage> getProductImagesByProductId(Long productId) throws Exception;
    List<Object[]> findProductsByIds(String ids);
}

