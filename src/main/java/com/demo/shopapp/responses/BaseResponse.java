package com.demo.shopapp.responses;


import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseResponse {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
