package com.demo.shopapp.domain.user.service.user;


import com.demo.shopapp.domain.user.dto.UserDTO;

import com.demo.shopapp.domain.user.dto.UserLoginDTO;
import com.demo.shopapp.domain.user.entity.Token;
import com.demo.shopapp.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface IUserService {

    User create(UserDTO uerDTO, boolean isAdmin) throws Exception;

    User getUserById(long id) throws Exception;

    Token login(UserLoginDTO userLoginDTO, HttpServletRequest request) throws Exception;

    Page<User> searchUsers(String keyWord, int page, int limit) ;
}


//Category createCategory(CategoryDTO category) throws Exception;
//Category getCategoryById(long id);
//Category updateCategory(long id, CategoryDTO category);
//
//List<Category> getAllCategories(int page, int limit);
//void deleteCategoryById(long id);