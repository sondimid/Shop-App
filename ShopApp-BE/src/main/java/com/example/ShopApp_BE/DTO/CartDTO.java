package com.example.ShopApp_BE.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CartDTO {
    List<CartDetailDTO> cartDetailDTOS;
}
