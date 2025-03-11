package com.example.ShopApp_BE.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO{
    @JsonProperty("productId")
    private Long productId;

    @JsonProperty("numberOfProducts")
    private Long numberOfProducts;

    private String color;

    private Double price;

    private Double totalMoney;

}
