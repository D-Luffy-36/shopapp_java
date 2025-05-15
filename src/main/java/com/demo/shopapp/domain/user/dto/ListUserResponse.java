package com.demo.shopapp.domain.user.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListUserResponse {
    private List<UserResponse> users;
    private int totalPages;
}
