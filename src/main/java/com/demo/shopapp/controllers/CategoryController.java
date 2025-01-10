package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.CategoryDTO;
import com.demo.shopapp.entities.Category;
import com.demo.shopapp.services.CategoryServices.CategorySevice;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("${api.prefix}/categories")

//@Validated
public class CategoryController {

    private final CategorySevice categoryService;

    public CategoryController(CategorySevice categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping()
    public ResponseEntity<?> create(
            @Valid @RequestBody CategoryDTO categoryDTO, BindingResult result) {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError -> FieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    "error = " + errorMessages.toString()
            );
        }
        try{
            Category newCategory = this.categoryService.createCategory(categoryDTO);
            // Tạo một Map để trả về thông báo
            Map<String, String> response = new HashMap<>();
            response.put("message: ", "Category created successfully");
            response.put("category: ", newCategory.getName().toLowerCase());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // hiển thị tất cả category
    @GetMapping()
    public ResponseEntity<List<Category>> list(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        List<Category> categories = this.categoryService.getAllCategories(page, limit);
        return
                ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}") // id -> path variable lấy id động
    public ResponseEntity<String> getCategoryById(@PathVariable long id) {
        return
                ResponseEntity.ok(
                        "ok"
                );
    }



    @PutMapping("/{id}")
    // nếu tham số truyền vào là 1 object
    public ResponseEntity<String> update(
            @PathVariable long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {

        Category currentCategory = this.categoryService.updateCategory(id, categoryDTO);
        return
                ResponseEntity.ok(
                        "update category with id: " + id + " successfully"
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        this.categoryService.deleteCategoryById(id);
        return
                ResponseEntity.ok(
                        "delete category with id: " + id + " successfully"
                );
    }



}
