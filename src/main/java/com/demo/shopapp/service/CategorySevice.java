package com.demo.shopapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.demo.shopapp.repository.CategoryRepository;

@Service
public class CategorySevice {
    @Autowired
    private CategoryRepository categoryRepository;
}
