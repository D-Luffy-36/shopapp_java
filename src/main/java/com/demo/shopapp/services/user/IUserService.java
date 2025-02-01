package com.demo.shopapp.services.user;


import com.demo.shopapp.dtos.UserDTO;

import com.demo.shopapp.dtos.UserLoginDTO;
import com.demo.shopapp.entities.User;
import org.springframework.transaction.annotation.Transactional;

public interface IUserService {

    User create(UserDTO uerDTO) throws Exception;


    User getUserById(long id) throws Exception;

    String login(UserLoginDTO userLoginDTO) throws Exception;
}


//Category createCategory(CategoryDTO category) throws Exception;
//Category getCategoryById(long id);
//Category updateCategory(long id, CategoryDTO category);
//
//List<Category> getAllCategories(int page, int limit);
//void deleteCategoryById(long id);