package com.demo.shopapp.services.category;

import com.demo.shopapp.domain.product.dto.CategoryDTO;
import com.demo.shopapp.domain.product.entity.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category) throws Exception;
    Category getCategoryById(long id);
    Category updateCategory(long id, CategoryDTO category);

    List<Category> getAllCategories(int page, int limit);
    void deleteCategoryById(long id);

}
