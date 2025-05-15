package com.demo.shopapp.domain.product.service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.shopapp.domain.product.dto.CategoryDTO;
import com.demo.shopapp.domain.product.entity.Category;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.demo.shopapp.domain.product.repository.CategoryRepository;

import java.util.List;

@Service

public class CategorySevice implements ICategoryService {
    private final CategoryRepository categoryRepository;

    public CategorySevice(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public Category createCategory(CategoryDTO category) throws RuntimeException {
        String name = category.getName().toLowerCase();
        boolean existed = categoryRepository.existsByName(name);
        if (existed) {
           throw new RuntimeException("Category already exists: " + name);
        }
        Category newCategory =
                Category.builder()
                .name(name)
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Category not found"));
    }

    @Transactional
    @Override
    public Category updateCategory(long id, CategoryDTO category) {
        Category currentCategory = getCategoryById(id);
        String name = category.getName().toLowerCase();
        Boolean exsisted = this.categoryRepository.existsByName(name);
        if (exsisted) {
            throw new RuntimeException("Category already exists: " + name);
        }
        currentCategory.setName(name);
        return categoryRepository.save(currentCategory);
    }

    @Override
    public List<Category> getAllCategories(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return categoryRepository.findAll(pageable).getContent();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteCategoryById(long id) {
        // soft delete
        Category currentCategory = getCategoryById(id);
        currentCategory.setName("");
        this.categoryRepository.save(currentCategory);
    }
}
