package com.demo.shopapp.controller;


import com.demo.shopapp.dto.UerDTO;
import com.demo.shopapp.dto.UserLoginDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody UerDTO userDTO, BindingResult result) {
        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();
                // check passWord and retypePassWord
                return ResponseEntity.badRequest().body(
                        "error = " + errorMessages.toString()
                );}
            if(!userDTO.getPassWord().equals(userDTO.getRetypePassWord())){
                return ResponseEntity.badRequest().body("wrong retypePassWord");
            }

            return ResponseEntity.ok().body("" +
                    "create new user");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok().body(
                "login successfully"
        );
    }
}
