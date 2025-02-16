package com.demo.shopapp.services.product;

import com.demo.shopapp.dtos.ProductDTO;
import com.demo.shopapp.dtos.ProductImageDTO;

import com.demo.shopapp.entities.Product;
import com.demo.shopapp.entities.ProductImage;
import com.demo.shopapp.exceptions.InvalidParamException;
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

