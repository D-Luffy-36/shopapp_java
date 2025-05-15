package com.demo.shopapp.domain.product.controller;

import com.demo.shopapp.domain.product.dto.ProductDTO;
import com.demo.shopapp.domain.product.dto.ProductImageDTO;
import com.demo.shopapp.domain.product.entity.Product;
import com.demo.shopapp.domain.product.entity.ProductImage;
import com.demo.shopapp.shared.exceptions.DataNotFoundException;
import com.demo.shopapp.domain.product.dto.ListProductResponse;
import com.demo.shopapp.domain.product.dto.ProductResponse;
import com.demo.shopapp.domain.product.service.ProductService;

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

import java.io.File;
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
            @RequestParam(defaultValue = "") String keyWord,
            @RequestParam(name = "category_id", defaultValue = "0") Long categoryId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        Page<Product> products =  this.productService.getAllProducts(keyWord, categoryId, page, limit);
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


    @GetMapping("/find")
    public Object listProductByIds(@RequestParam("ids") String ids) throws Exception {
        try{
            return this.productService.findProductsByIds(ids)
                    .stream()
                    .map(row -> ProductResponse.builder()
                            .id(((Number) row[0]).longValue())  // Ép kiểu về Long
                            .name((String) row[1])              // Ép kiểu về String
                            .price(((Number) row[2]).doubleValue()) // Ép kiểu về Double
                            .thumbnail((String) row[3])         // Ép kiểu về String
                            .build()
                    )
                    .toList();

        } catch (Exception e) {
            return e.getMessage();
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

    private String storeFile(MultipartFile file, String folder) throws IOException {
        // Kiểm tra file có hợp lệ không
        if (!isImageFile(file) || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new IOException("Invalid image file");
        }

        // Normalize tên file để tránh ký tự nguy hiểm
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        // Tạo tên file duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Định nghĩa đường dẫn thư mục lưu file
        Path folderPath = Paths.get(UPLOAD_DIR, folder).normalize();

        // Tạo thư mục nếu chưa tồn tại
        Files.createDirectories(folderPath);

        // Tạo đường dẫn file đích
        Path destination = folderPath.resolve(uniqueFilename).normalize();

        // Sao chép file vào thư mục
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/"));
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

    @PostMapping(value = "/uploads/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImgs(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {

        files = files == null ? new ArrayList<>() : files;
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files provided.");
        }

        try {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cannot find product with id: " + id);
            }

            List<ProductImage> existingImages = existingProduct.getImages();
            int currentImageCount = existingImages.size();
            int newImageCount = files.size();
            int totalImageCount = currentImageCount + newImageCount;

            // Xóa ảnh cũ nếu vượt quá MAX_IMAGES
            if (totalImageCount > ProductImage.MAX_IMAGES) {
                existingImages.sort(Comparator.comparingLong(ProductImage::getId));
                int imagesToRemove = totalImageCount - ProductImage.MAX_IMAGES;

                for (int i = 0; i < imagesToRemove; i++) {
                    ProductImage oldestImage = existingImages.get(0);
                    String filePath = UPLOAD_DIR;
                    Path path = Paths.get(filePath);
                    if (Files.exists(path)) {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete file: " + filePath);
                            e.printStackTrace();
                        }
                    }

                    // Xóa ảnh khỏi DB
                    productService.deleteProductImageById(oldestImage.getId());

                    // Xóa khỏi danh sách hiện tại
                    existingImages.remove(0);
                }
            }

            List<ProductImage> newImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File size too large: " + file.getSize() + " bytes");
                }

                if (!isImageFile(file)) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }

                String fileName = storeFile(file, "products");

                ProductImageDTO newProductImageDTO = ProductImageDTO.builder()
                        .imageUrl(fileName)
                        .productId(id)
                        .build();

                ProductImage newProductImage = productService.createProductImage(id, newProductImageDTO);
                newImages.add(newProductImage);
            }

            // Nếu chưa có thumbnail, gán thumbnail là ảnh đầu tiên
            if (existingProduct.getThumbnail() == null || existingProduct.getThumbnail().isEmpty()) {
                if (!existingProduct.getImages().isEmpty()) {
                    existingProduct.setThumbnail(existingProduct.getImages().get(0).getImageUrl());
                    productService.save(existingProduct);
                }
            }

            return ResponseEntity.ok(newImages);
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
