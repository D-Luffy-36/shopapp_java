package com.demo.shopapp.services.user;


import com.demo.shopapp.dtos.UserDTO;

import com.demo.shopapp.entities.User;

public interface IUserService {
    User create(UserDTO uerDTO) throws RuntimeException;

    User getUserById(long id) throws RuntimeException;

    String Login(String phone, String password) throws RuntimeException;
}


//Category createCategory(CategoryDTO category) throws Exception;
//Category getCategoryById(long id);
//Category updateCategory(long id, CategoryDTO category);
//
//List<Category> getAllCategories(int page, int limit);
//void deleteCategoryById(long id);