package com.demo.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UerDTO {
    @JsonProperty("fullname")
    private String fullName;

    @NotBlank(message = "phone number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;
    @NotBlank(message = "pass word can not be blank")
    @JsonProperty("password")
    private String passWord;

    @NotBlank(message = "pass word can not be blank")
    @JsonProperty("retype_password")
    private String retypePassWord;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private Integer facebookAccountId;

    @JsonProperty("google_account_id")
    private Integer googleAccountId;

    @NotNull(message = "role id is required")
    @JsonProperty("role_id")
    private Long roleId;
}

//  [fullname] VARCHAR(150),
//  [phone_number] VARCHAR(10) NOT NULL,
//  [address] VARCHAR(255) DEFAULT '',
//  [password] VARCHAR(255) NOT NULL DEFAULT '',
//  [created_at] DATETIME DEFAULT 'GETDATE()',
//  [updated_at] DATETIME DEFAULT 'GETDATE()',
//  [is_active] BIT DEFAULT (1),
//  [date_of_birth] DATE NOT NULL,
//  [facebook_account_id] BIGINT DEFAULT (0),
//  [google_account_id] BIGINT DEFAULT (0),
//  [role_id] BIGINT