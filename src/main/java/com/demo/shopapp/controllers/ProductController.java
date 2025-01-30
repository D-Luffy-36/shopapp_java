package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.ProductDTO;
import com.demo.shopapp.dtos.ProductImageDTO;
import com.demo.shopapp.entities.Product;
import com.demo.shopapp.entities.ProductImage;
import com.demo.shopapp.exceptions.DataNotFoundException;
import com.demo.shopapp.responses.product.ListProductResponse;
import com.demo.shopapp.responses.product.ProductResponse;
import com.demo.shopapp.services.product.ProductService;

import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("${api.prefix}/products")
//@Validated
public class ProductController {
    private final ProductService productService;

    private static final String UPLOAD_DIR = "uploads/";

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<ListProductResponse> list(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        Page<Product> products =  this.productService.getAllProducts(page, limit);
        ListProductResponse listProductResponses =  ListProductResponse.builder()
                .products(products.getContent()
                .stream()
                .map(
                        ProductResponse::fromProduct).toList()
                )
                .totalPages(products.getTotalPages())
                .build();
        return ResponseEntity.ok(listProductResponses);

    }

    @GetMapping("/{id}") // id -> path variable lấy id động
    public ResponseEntity<?> detail(@PathVariable("id") long id) {
        try{
            Product existingProduct = this.productService.getProductById(id);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));

        }catch(DataNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody ProductDTO productDTO,
            BindingResult result) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body(
                        "error = " + errorMessages.toString()
                );}

            Product newProduct = this.productService.createProduct(productDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "create product succesfully");
            response.put("product", ProductResponse.fromProduct(newProduct));

            return  ResponseEntity.ok(response);

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    // nếu tham số truyền vào là 1 object
    public ResponseEntity<?> update(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO
    ) {
        try{
            String message =  "update product: " + id + " successfully";
            Product product = this.productService.updateProduct(id, productDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", message);
            response.put("product", ProductResponse.fromProduct(product));
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        // xóa mềm
        try{
            Product existingProduct = this.productService.getProductById(id);
            this.productService.deleteProduct(id);
            return ResponseEntity.ok("delete product id: " + id  + " succesfully");

        }catch(DataNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file) || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new IOException("invalid image file");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // đường dẫn tới thư mục mà bạn lưu file
        Path uploadDir = Paths.get("uploads");
        // kiểm tra và tạo thư mục nếu nó không tồn tại
        if(!Files.exists(uploadDir)){
            Files.createDirectory(uploadDir);
        }
        // tạo ra 1 đường dẫn tới thư mục upload
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // sao chép file vào đường dẫn  /uploads
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/"));
    }

    @PostMapping(value = "/uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImgs(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) throws IOException, Exception {

        files = files == null ? new ArrayList<>() : files;
        if(files.size() > ProductImage.MAX_IMAGES){
            return ResponseEntity.badRequest().body("max image just 5 files");
        }
        try{
            List<ProductImage> images = new ArrayList<>();
            // check size và format đã ổn chưa
            for (MultipartFile file : files) {
                // check file rỗng
                if(file.getSize() == 0){
                    continue;
                }
                if(file != null ) {
                    // kích thước > 10 MB
                    if(file.getSize() > 10 * 1024 * 1024) {
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                                "error = " + file.getSize() + " MB");
                    }
                    if(!isImageFile(file)) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                                "file must be an image");
                    }
                    // lưu file
                    String fileName = storeFile(file);
//                    fileNames.add(fileName);

                    Product existingProduct = this.productService.getProductById(id);
                    if(existingProduct == null){
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cant not found product");
                    }

                    // lưu vào bảng product_images
                    ProductImageDTO newProductImageDTO = ProductImageDTO.builder()
                            .imageUrl(fileName)
                            .productId(id)
                            .build();
                    ProductImage newProductImage = this.productService.createProductImage(id, newProductImageDTO);
                    images.add(newProductImage);
                }
            }
            return ResponseEntity.ok(images);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


//    @PostMapping("/generateFakeProducts")
    public ResponseEntity<?> generateFakeProducts() {
        try{
            final int MAX = 1000000;
            Faker faker = new Faker();
            Random random = new Random();
            for(int i = 0; i < MAX; i++){
                String name = faker.commerce().productName();
                if(this.productService.existingProductName(name)){
                    continue;
                }
                Float price = random.nextFloat() * (10000000 - 0) + 0;
                String thumbnail = faker.internet().avatar(); // Link ảnh giả
                String description = faker.lorem().paragraph(); // Mô tả
                Float discount = random.nextFloat() * (100 - 0) + 0; // Giảm giá (0 đến 100)
                Long categoryId = (long) (random.nextInt(6) + 1);  // Random từ 1 đến 6
                ProductDTO productDTO = ProductDTO.builder()
                        .name(name)
                        .price(price)
                        .thumbnail(thumbnail)
                        .discount(discount)
                        .categoryId(categoryId)
                        .description(description)
                        .build();
                this.productService.createProduct(productDTO);
            }
            return ResponseEntity.ok("create " + MAX + " products" + " successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
