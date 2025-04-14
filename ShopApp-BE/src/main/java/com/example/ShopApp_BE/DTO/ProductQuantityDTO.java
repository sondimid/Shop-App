package com.example.ShopApp_BE.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQuantityDTO {
    private Long productId;

    private Integer numberOfProduct;
}
