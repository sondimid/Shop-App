package com.example.ShopApp_BE.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailDTO{
    private Long productId;

    private String color;

    private Integer numberOfProducts;
}
