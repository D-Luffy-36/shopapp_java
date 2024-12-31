package com.demo.shopapp.controller;

import com.demo.shopapp.dto.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/products")
//@Validated
public class ProductController {
    //    @Autowired
//    private CategorySevice categorySevice;
    // hiển thị tất cả category
    @GetMapping()
    public ResponseEntity<String> list(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        return
                ResponseEntity.ok(
                        "page = " + page + " limit = " + limit
                );
    }

    @GetMapping("/{id}") // id -> path variable lấy id động
    public ResponseEntity<String> detail(@PathVariable("id") long id) {
        return
                ResponseEntity.ok(
                        "product" + id
                );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(@Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result) {

//        System.out.println("Received name: " + productDTO.getName());
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body(
                        "error = " + errorMessages.toString()
                );}
            List<MultipartFile> files = productDTO.getFiles();
            files = files == null ? new ArrayList<>() : files;
            List<String> fileNames = new ArrayList<>();

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
                    String contentType = file.getContentType();
                    if(contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                                "file must be an image");
                    }
                    // lưu file
                    String fileName = storeFile(file);
                    fileNames.add(fileName);
                    // lưu vào bảng product_images
                }
            }


            return  ResponseEntity.ok(
                            "create product succesfully");

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    // nếu tham số truyền vào là 1 object
    public ResponseEntity<String> update(
            @PathVariable long id,
            ProductDTO productDTO
    ) {
        return
                ResponseEntity.ok(
                        "update product: " + id
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        return
                ResponseEntity.ok(
                        "delete product" + id
                );
    }

    private String storeFile(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
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

}
