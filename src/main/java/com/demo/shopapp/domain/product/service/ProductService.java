package com.demo.shopapp.domain.product.service;

import com.demo.shopapp.domain.product.dto.ProductDTO;
import com.demo.shopapp.domain.product.dto.ProductImageDTO;
import com.demo.shopapp.domain.product.entity.Category;
import com.demo.shopapp.domain.product.entity.Product;
import com.demo.shopapp.entities.ProductImage;
import com.demo.shopapp.shared.exceptions.DataNotFoundException;
import com.demo.shopapp.shared.exceptions.InvalidParamException;
import com.demo.shopapp.repositorys.CategoryRepository;
import com.demo.shopapp.repositorys.ProductImageRepository;
import com.demo.shopapp.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional
    @Override
    public Product createProduct(ProductDTO productDTO) {

        Category category = this.categoryRepository.findCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(Double.valueOf(productDTO.getPrice()))
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(category)
                .build();

        return this.productRepository.save(newProduct);
    }


    @Override
    public Page<Product> getAllProducts(String keyWord, Long category_id ,int page, int limit) {
        if (page <= 0) {
            page = 1; // Đặt giá trị mặc định
        }

        Pageable pageable = PageRequest.of(page - 1, limit,
//                Sort.by("createdAt").descending()
                Sort.by("id").ascending());
        return this.productRepository.searchProducts(keyWord, category_id, pageable);

    }

    @Override
    public Product getProductById(Long id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + id));
    }


    @Override
    public List<ProductImage> getProductImagesByProductId(Long productId) {
        return this.productImageRepository.findByProductId(productId);
    }

    public List<Object[]> findProductsByIds(String ids) {
        List<Long> productIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.productRepository.findProductByIds(productIds);
    }



    @Transactional
    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product currentProduct = this.productRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Product not found with id: " + id));
        // copy các thuốc tính từ dto -> current product
        // Model Mapper

        Category category = this.categoryRepository.findCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + productDTO.getCategoryId()));

        currentProduct.setName(productDTO.getName());
        currentProduct.setPrice(Double.valueOf(productDTO.getPrice()));
        currentProduct.setDescription(productDTO.getDescription());
        currentProduct.setThumbnail(productDTO.getThumbnail());
        currentProduct.setCategory(category);

        return this.productRepository.save(currentProduct);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        // xóa cứng
        Product currentProduct = this.productRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Product not found with id: " + id));
        this.productRepository.delete(currentProduct);
    }

    @Transactional
    public ProductImage createProductImage(Long productId,
                                           ProductImageDTO productImageDTO ) throws InvalidParamException {

        Product existingProduct = this.productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));

        ProductImage newProductImage = ProductImage.builder()
                .imageUrl(productImageDTO.getImageUrl())
                .product(existingProduct)
                .build();
        // không cho insert quá 5 ảnh cho 1 sản phầm
        List<ProductImage> images = this.productImageRepository.findByProductId(productId);
        if(images.size() >= 5){
            throw new InvalidParamException("number param <= 5");
        }
        return this.productImageRepository.save(newProductImage);

    }

    public boolean existingProductName(String name){
        return this.productRepository.existsByName(name);
    }

//    boolean existingProduct(Long productId) {
//        Optional<Product> currentProduct = this.productRepository.findById(productId);
//        return currentProduct.isPresent();
//    }


}


