package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.request.CategoryDTO;
import com.demo.shopapp.entities.Category;
import com.demo.shopapp.dtos.responses.ResponseObject;
import com.demo.shopapp.services.category.CategorySevice;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@RestController
@RequestMapping("${api.prefix}/categories")
@AllArgsConstructor
//@Validated
public class CategoryController {

    private final CategorySevice categoryService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

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
//            @RequestParam("page") int page,
//            @RequestParam("limit") int limit
    ) {
        List<Category> categories = this.categoryService.getAllCategories();
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
    public ResponseObject<Category> update(
            @PathVariable long id,
            @Valid @RequestBody CategoryDTO categoryDTO, HttpServletRequest request
    ) {
        try{
            Category currentCategory = this.categoryService.updateCategory(id, categoryDTO);
            Locale locale = localeResolver.resolveLocale(request);
            return ResponseObject.<Category>builder()
                    .status(HttpStatus.OK)
                    .message(messageSource.getMessage("category.update_category.update_successfully", null, locale))
                    .data(currentCategory)
                    .build();
        } catch (Exception e) {
            return ResponseObject.<Category>builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        }
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
