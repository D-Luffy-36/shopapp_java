package com.demo.shopapp.controller;

import com.demo.shopapp.dto.CategoryDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/categories")
//@Validated
public class CategoryController {
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
    public ResponseEntity<String> getCategoryById(@PathVariable long id) {
        return
                ResponseEntity.ok(
                        "ok"
                );
    }

    @PostMapping()
    public ResponseEntity<String> create(
           @Valid @RequestBody CategoryDTO categoryDTO, BindingResult result) {
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
                        "create category: " + categoryDTO
                );
    }

    @PutMapping("/{id}")
    // nếu tham số truyền vào là 1 object
    public ResponseEntity<String> update(
            @PathVariable long id,
            CategoryDTO categoryDTO
    ) {
        return
                ResponseEntity.ok(
                        "update category"
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        return
                ResponseEntity.ok(
                        "delete category"
                );
    }



}
