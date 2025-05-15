package com.demo.shopapp.domain.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email; // Email hoặc SĐT

    @NotBlank(message = "pass word can not be blank")
    @JsonProperty("password")
    private String password;

    // Facebook Account Id, not mandatory, can be blank
//    @JsonProperty("facebook_account_id")
//    private String facebookAccountId;

    // Google Account Id, not mandatory, can be blank
//    @JsonProperty("google_account_id")
//    private String googleAccountId;

    //For Google, Facebook login
    // Full name, not mandatory, can be blank
    @JsonProperty("fullname")
    private String fullname;

    // Profile image URL, not mandatory, can be blank
    @JsonProperty("profile_image")
    private String profileImage;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }
    // Kiểm tra facebookAccountId có hợp lệ không
//    public boolean isFacebookAccountIdValid() {
//        return facebookAccountId != null && !facebookAccountId.isEmpty();
//    }

    // Kiểm tra googleAccountId có hợp lệ không
//    public boolean isGoogleAccountIdValid() {
//        return googleAccountId != null && !googleAccountId.isEmpty();
//    }

}
