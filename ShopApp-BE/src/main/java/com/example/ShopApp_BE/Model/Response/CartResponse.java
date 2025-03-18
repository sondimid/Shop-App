package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.CartEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse extends AbstractResponse{
    private Double totalMoney;

    private List<CartDetailResponse> cartDetailResponses;

    public static CartResponse fromCartEntity(CartEntity cartEntity) {
        List<CartDetailResponse> cartDetailResponses =
                CartDetailResponse.fromCartDetailEntities(cartEntity.getCartDetailEntities());
        CartResponse cartResponse = CartResponse.builder()
                .cartDetailResponses(cartDetailResponses)
                .totalMoney(cartDetailResponses.stream().mapToDouble(CartDetailResponse::getTotalMoney).sum())
                .build();
        cartResponse.setCreatedAt(cartEntity.getCreatedAt());
        cartResponse.setUpdatedAt(cartEntity.getUpdatedAt());
        return cartResponse;
    }
}
