package com.demo.shopapp.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Trỏ đường dẫn ảo "/products/**" đến thư mục "uploads/products/"
        Path uploadDir = Paths.get("uploads/products");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("images/products/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(31536000);

    }
}
