package com.demo.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("fullname")
    private String fullName;

    @NotBlank(message = "phone number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @NotBlank(message = "pass word can not be blank")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "pass word can not be blank")
    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")  // Định dạng ngày theo "yyyy-MM-dd"
    private LocalDate dateOfBirth;

    @JsonProperty("facebook_account_id")
    @Column(unique = true)
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    @Column(unique = true)
    private String googleAccountId;

    @NotNull(message = "role id is required")
    @JsonProperty("role_id")
    private Long roleId;

    // Sử dụng phương thức getter duy nhất
    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getFacebookAccountId() {
        return facebookAccountId;
    }

    public String getGoogleAccountId() {
        return googleAccountId;
    }



    // Lược bỏ getter cho "password" và "retypePassword" nếu không cần
}
