package com.example.ShopApp_BE.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO{
    private Long productId;

    private Integer numberOfProducts;

    private String color;
}
