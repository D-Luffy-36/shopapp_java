package com.demo.shopapp.controller;

import com.demo.shopapp.dto.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @PostMapping()
    public ResponseEntity<String> create(
            @Valid @RequestBody ProductDTO productDTO, BindingResult result) {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError -> FieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    "error = " + errorMessages.toString()
            );
        }
        return
                ResponseEntity.ok(
                        "create product: " + productDTO
                );
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



}
