package com.demo.shopapp.controllers;

import com.demo.shopapp.components.JwtTokenUtils;
import com.demo.shopapp.dtos.UserDTO;
import com.demo.shopapp.dtos.UserLoginDTO;
import com.demo.shopapp.entities.User;

import com.demo.shopapp.responses.ResponseObject;
import com.demo.shopapp.responses.user.LoginResponse;
import com.demo.shopapp.responses.user.UserResponse;
import com.demo.shopapp.services.user.UserService;
import com.demo.shopapp.components.LocalizationUtils;

import com.demo.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor

// jwtFilter đã chec token rồi
public class UserController {
    private final UserService userService;
    private final LocalizationUtils localizationUtils;


    @PostMapping("/register")
    public ResponseObject<?> register(@Valid @RequestBody UserDTO userDTO,
                                    BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();
                // check passWord and retypePassWord
                return ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(errorMessages.toString())
                        .build();
                }

            // check nhập lại password
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(this.localizationUtils.getLocalizationMessage(MessageKeys.REGISTER_PASSWORD_NOT_MATCH))
                        .build();
            }
            User newUser = this.userService.create(userDTO);
            UserResponse userResponse = UserResponse.fromUser(newUser);

            return ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(userResponse)
                    .message(this.localizationUtils
                            .getLocalizationMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                    .build();
        } catch (Exception e) {
            return ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/login")
    public ResponseObject<LoginResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try{
            String token = this.userService.login(userLoginDTO);

            return ResponseObject.<LoginResponse>builder()
                    .data(LoginResponse.builder().token(token).build())
                    .message(this.localizationUtils
                            .getLocalizationMessage(MessageKeys.LOGIN_SUCCESFULLY))
                    .status(HttpStatus.OK)
                    .build();

        }catch (Exception e){
            return ResponseObject.<LoginResponse>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/details")
    public ResponseObject<UserResponse> getDetailsUser(
            @RequestHeader("Authorization") String bearerToken) {

        try{
            String token = bearerToken.substring(7); // cắt Bearer
            User user = this.userService.getUserDetailsFromToken(token);
            UserResponse userResponse = UserResponse.fromUser(user);
            return ResponseObject.<UserResponse>builder()
                    .status(HttpStatus.OK)
                    .data(userResponse)
                    .build();
        } catch (Exception e) {
            return ResponseObject.<UserResponse>builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        }
    }


    @PostMapping("/update")
    public ResponseObject<?> update(
            @RequestHeader("Authorization") String bearerToken,
            @Valid @RequestBody UserDTO userDTO,
                                      BindingResult result) {

        try{
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                // check passWord and retypePassWord
                return ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(errorMessages.toString())
                        .build();
            }

            String token = bearerToken.substring(7);
            User user = this.userService.UpdateUser(token, userDTO);
            UserResponse userResponse = UserResponse.fromUser(user);

            return ResponseObject.<UserResponse>builder()
                    .status(HttpStatus.OK)
                    .data(userResponse)
                    .build();
        } catch (Exception e) {
            return ResponseObject.<UserResponse>builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        }

    }


}
