package com.demo.shopapp.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private static final String UPLOAD_DIR = "uploads/";
    // hàm xem nội dung của ảnh
    @GetMapping("/{imageName}")
    public ResponseEntity<?> viewImageName(@PathVariable String imageName) throws IOException {
        Path imagePath = Paths.get(UPLOAD_DIR + imageName);

        if (!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);
        String contentType = Files.probeContentType(imagePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }
}
