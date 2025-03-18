package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CartDetailEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CartDetailResponse extends AbstractResponse{
    private Integer numberOfProducts;

    private String color;

    private Double price;

    private Double totalMoney;

    private ProductResponse productResponse;

    public static CartDetailResponse fromCartDetailEntity(CartDetailEntity cartDetailEntity) {
        CartDetailResponse cartDetailResponse = CartDetailResponse.builder()
                .numberOfProducts(cartDetailEntity.getNumberOfProducts())
                .color(cartDetailEntity.getColor())
                .price(cartDetailEntity.getProductEntity().getPrice())
                .totalMoney(cartDetailEntity.getNumberOfProducts() * cartDetailEntity.getProductEntity().getPrice() * (100 - cartDetailEntity.getProductEntity().getDiscount()) / 100.0)
                .productResponse(ProductResponse.fromProductEntity(cartDetailEntity.getProductEntity())).build();
        cartDetailResponse.setCreatedAt(cartDetailEntity.getCreatedAt());
        cartDetailResponse.setUpdatedAt(cartDetailEntity.getUpdatedAt());
        return cartDetailResponse;
    }

    public static List<CartDetailResponse> fromCartDetailEntities(List<CartDetailEntity> cartDetailEntities) {
        return cartDetailEntities.stream().map(CartDetailResponse::fromCartDetailEntity).toList();
    }
}
