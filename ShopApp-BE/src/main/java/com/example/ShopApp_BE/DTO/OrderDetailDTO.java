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

    private String name;

    private Long numberOfProducts;

    private Double price;

    private Double discount;

    private Double totalMoney;

    private String color;
}
