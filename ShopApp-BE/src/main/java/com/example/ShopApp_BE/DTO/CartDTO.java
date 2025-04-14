package com.example.ShopApp_BE.DTO;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class CartDTO implements Serializable {
    List<CartDetailDTO> cartDetailDTOS = new ArrayList<>();

    List<Long> idDeletes = new ArrayList<>();
}
