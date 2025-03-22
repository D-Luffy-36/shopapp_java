package com.demo.shopapp.services.user;


import com.demo.shopapp.dtos.request.UserDTO;

import com.demo.shopapp.dtos.request.UserLoginDTO;
import com.demo.shopapp.entities.User;
import org.springframework.data.domain.Page;

public interface IUserService {

    User create(UserDTO uerDTO, boolean isAdmin) throws Exception;

    User getUserById(long id) throws Exception;

    String login(UserLoginDTO userLoginDTO) throws Exception;

    Page<User> searchUsers(String keyWord, int page, int limit) ;
}


//Category createCategory(CategoryDTO category) throws Exception;
//Category getCategoryById(long id);
//Category updateCategory(long id, CategoryDTO category);
//
//List<Category> getAllCategories(int page, int limit);
//void deleteCategoryById(long id);