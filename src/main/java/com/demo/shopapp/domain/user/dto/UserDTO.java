package com.demo.shopapp.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @NotBlank(message = "password can not be blank")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "password can not be blank")
    @JsonProperty("retype_password")
    private String retypePassword;

    // boolean mặc định là false
    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

//    @JsonProperty("facebook_account_id")
//    @Column(unique = true)
//    private String facebookAccountId;
//
//    @JsonProperty("google_account_id")
//    @Column(unique = true)
//    private String googleAccountId;


    @JsonProperty("roles")
    private Set<String> roleNames = new HashSet<>();

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



    public String getEmail() {
        return email;
    }


    // Lược bỏ getter cho "password" và "retypePassword" nếu không cần
}
