package com.demo.shopapp.domain.image;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;

@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private static final String UPLOAD_IMG_PRODUCT_DIR = "uploads/products/";
    private static final String UPLOAD_IMG_AVT_DIR = "uploads/avatar/";

    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> viewImageName(@PathVariable String imageName) throws IOException {
        Path imagePath = Paths.get(UPLOAD_IMG_PRODUCT_DIR, imageName);

        // Debug đường dẫn
        System.out.println("🔍 Checking file: " + imagePath.toAbsolutePath());

        if (!Files.exists(imagePath)) {
            System.out.println("❌ File not found: " + imagePath.toAbsolutePath());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String contentType = Files.probeContentType(imagePath);
        if (contentType == null) {
            contentType = "application/octet-stream"; // Mặc định nếu không xác định được loại file
        }

        // Stream ảnh thay vì load toàn bộ vào bộ nhớ
        InputStream inputStream = Files.newInputStream(imagePath);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"") // Hiển thị ảnh trong trình duyệt
                .body(resource);
    }
}
