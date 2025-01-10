package com.demo.shopapp.services.ProductServices;

import com.demo.shopapp.dtos.ProductDTO;
import com.demo.shopapp.dtos.ProductImageDTO;
import com.demo.shopapp.entities.Category;
import com.demo.shopapp.entities.Product;
import com.demo.shopapp.entities.ProductImage;
import com.demo.shopapp.responses.ProductResponse;
import com.demo.shopapp.exceptions.DataNotFoundException;
import com.demo.shopapp.exceptions.InvalidParamException;
import com.demo.shopapp.repositorys.CategoryRepository;
import com.demo.shopapp.repositorys.ProductImageRepository;
import com.demo.shopapp.repositorys.ProductRepository;
import com.demo.shopapp.responses.ListProductResponses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
    }
    @Override
    public Product createProduct(ProductDTO productDTO)  throws Exception{
//        Boolean existsByName = this.productRepository.existsByName(productDTO.getName());
//        if (existsByName) {
//            throw new Exception("existed product name");
//        }

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
    public Page<Product> getAllProducts(int page, int limit) {
        if (page <= 0) {
            page = 1; // Đặt giá trị mặc định
        }

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Product> products = this.productRepository.findAll(pageable);

        return products;
    }

    @Override
    public Product getProductById(Long id) {
        Product existingProduct = this.productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + id));
       return existingProduct;
    }

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

    @Override
    public void deleteProduct(Long id) {
        // xóa cứng
        Optional<Product> currentProduct = this.productRepository.findById(id);
        currentProduct.ifPresent(this.productRepository::delete);
    }

    public ProductImage createProductImage(Long productId,
                                           ProductImageDTO productImageDTO ) {

        Product existingProduct = this.productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));

        ProductImage newProductImage = ProductImage.builder()
                .imageUrl(productImageDTO.getImageUrl())
                .product(existingProduct)
                .build();
        // không cho insert quá 5 ảnh cho 1 sản phầm
        List<ProductImage> images = this.productImageRepository.findByProductId(productId);
        if(images.size() >= 5){
            throw  new InvalidParamException("number param <= 5");
        }
        return this.productImageRepository.save(newProductImage);

    }

//    boolean ExistingProduct(Long productId) {
//        Optional<Product> currentProduct = this.productRepository.findById(productId);
//        return currentProduct.isPresent();
//    }



}


