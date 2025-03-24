package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.request.UserDTO;
import com.demo.shopapp.dtos.request.UserLoginDTO;
import com.demo.shopapp.dtos.request.AdminUserUpdateRequest;
import com.demo.shopapp.entities.Token;
import com.demo.shopapp.entities.User;

import com.demo.shopapp.dtos.responses.ResponseObject;
import com.demo.shopapp.dtos.responses.user.ListUserResponse;
import com.demo.shopapp.dtos.responses.user.LoginResponse;
import com.demo.shopapp.dtos.responses.user.UserResponse;
import com.demo.shopapp.services.user.UserService;
import com.demo.shopapp.components.LocalizationUtils;

import com.demo.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor

// jwtFilter đã check token rồi
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
            User newUser = this.userService.create(userDTO, false);
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

    @PostMapping("/admin/create")
    public ResponseObject<?> create(@Valid @RequestBody UserDTO userDTO,
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
            User newUser = this.userService.create(userDTO, true);
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
    public ResponseObject<LoginResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        try{
            Token token = this.userService.login(userLoginDTO, request);

            return ResponseObject.<LoginResponse>builder()
                    .data(LoginResponse.builder()
                            .token(token.getToken())
                            .refreshToken(token.getRefreshToken())
                            .tokenType(token.getTokenType())
                            .build())
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

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_USER')")
    @GetMapping("/details")
    public ResponseObject<UserResponse> getDetailsUser(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam(value = "userId", required = false) Long userId) {

        try{
            String token = bearerToken.substring(7); // cắt Bearer

            User user;
            if (userId != null) {
                user = userService.getUserDetailsForAdmin(token, userId);
            } else {
                user = userService.getUserDetailsFromToken(token);
            }

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

    // Người dùng tự cập nhật thông tin cá nhân → Chỉ cập nhật tên, số điện thoại, địa chỉ.
    // Người dùng có thể cập nhật mật khẩu, nhưng phải nhập lại (retypePassword).

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_USER')")
    @PutMapping("/update")
    public ResponseObject<?> updateOwnProfile(@RequestBody UserDTO userDTO, @RequestHeader("Authorization") String bearerToken) {
        try {

            User updatedUser = userService.updateUserProfile(bearerToken, userDTO);
            return ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(UserResponse.fromUser(updatedUser))
                    .message("User updated successfully")
                    .build();
        } catch (Exception e) {
            return ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/{userId}")
    public ResponseObject<?> updateUserByAdmin(@PathVariable Long userId, @RequestBody AdminUserUpdateRequest request) {
        try {
            User updatedUser = userService.updateUserByAdmin(userId, request);
            return ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(UserResponse.fromUser(updatedUser))
                    .message("User updated successfully by admin")
                    .build();
        } catch (Exception e) {
            return ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        }
    }



    // just admin can see all users
    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> list(
            @RequestParam (value = "keyWord", required = false, defaultValue = "") String keyWord,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit
    )  {
        Page<User> users =  this.userService.searchUsers(keyWord, page, limit);
        ListUserResponse listUserResponse = ListUserResponse.builder()
                .users(users.getContent()
                        .stream()
                        .map(UserResponse::fromUser)
                        .toList())
                .totalPages(users.getTotalPages())
                .build();
        return ResponseEntity.ok(listUserResponse);
    }

}
