package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.UserDTO;
import com.demo.shopapp.dtos.UserLoginDTO;
import com.demo.shopapp.entities.User;

import com.demo.shopapp.services.UserServices.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
            if (!userDTO.getPassWord().equals(userDTO.getRetypePassWord())) {
                return ResponseEntity.badRequest().body("wrong retypePassWord");
            }
            User newUser = this.userService.create(userDTO);
            // Tạo Map để chứa response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("data", newUser);

            return ResponseEntity.ok().body(response);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
//        String token = this.userService.Login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassWord());
        return ResponseEntity.ok().body(
                "login successfully"
        );
    }
}
