package com.example.ShopApp_BE.Model.Response;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AbstractResponse {
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
