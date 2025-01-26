package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.UserDTO;
import com.demo.shopapp.dtos.UserLoginDTO;
import com.demo.shopapp.entities.User;

import com.demo.shopapp.responses.ResponseObject;
import com.demo.shopapp.responses.orderDetail.OrderDetailResponse;
import com.demo.shopapp.responses.user.LoginResponse;
import com.demo.shopapp.responses.user.UserResponse;
import com.demo.shopapp.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO,
                                    BindingResult result) {
        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();
                // check passWord and retypePassWord
                return ResponseEntity.badRequest().body(
                        "error = " + errorMessages.toString()
                );}
            // check nhập lại password
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("wrong retypePassWord");
            }

            User newUser = this.userService.create(userDTO);
            UserResponse userResponse = UserResponse.fromUser(newUser);
            // Tạo Map để chứa response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("data", userResponse);

            return ResponseEntity.ok().body(response);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody UserLoginDTO userLoginDTO,
                               HttpServletRequest request) {
        try{
            String token = this.userService.login(userLoginDTO);
            Locale locale = localeResolver.resolveLocale(request);
            return LoginResponse.builder()
                    .message(messageSource.getMessage("user.login.login_successfully", null, locale))
                    .token(token)
                    .build();

        }catch (Exception e){
            return LoginResponse.builder()
                    .message(e.getMessage())
                    .token("")
                    .build();

        }
    }
}
