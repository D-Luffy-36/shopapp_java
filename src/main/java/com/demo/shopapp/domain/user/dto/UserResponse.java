package com.demo.shopapp.domain.user.dto;

import com.demo.shopapp.domain.role.entity.Role;
import com.demo.shopapp.domain.user.entity.User;
import com.demo.shopapp.shared.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends BaseResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")  // Định dạng ngày theo "yyyy-MM-dd"
    private LocalDate dateOfBirth;

    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    private String googleAccountId;

    @JsonProperty("roles")
    private Set<String> roles = new HashSet<>();

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
//                .profileImage(user.getProfileImage())
                .active(user.getIsActive())
                .dateOfBirth(LocalDate.from(user.getDateOfBirth()))
                .roles(
                        user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
