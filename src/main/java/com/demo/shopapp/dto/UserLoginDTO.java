package com.demo.shopapp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @NotBlank(message = "phone number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;


    @NotBlank(message = "pass word can not be blank")
    @JsonProperty("password")
    private String passWord;
}
