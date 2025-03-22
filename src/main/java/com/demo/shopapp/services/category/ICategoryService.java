package com.demo.shopapp.services.category;

import com.demo.shopapp.dtos.request.CategoryDTO;
import com.demo.shopapp.entities.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category) throws Exception;
    Category getCategoryById(long id);
    Category updateCategory(long id, CategoryDTO category);

    List<Category> getAllCategories(int page, int limit);
    void deleteCategoryById(long id);

}
