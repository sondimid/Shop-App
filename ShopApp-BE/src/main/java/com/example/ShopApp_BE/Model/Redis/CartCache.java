package com.example.ShopApp_BE.Model.Redis;

import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Model.Response.*;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("Cart")
public class CartCache extends AbstractResponse implements Serializable {
    @Id
    private String id;

    private Long cartId;

    private Double totalMoney;

    private List<CartDetailResponse> cartDetailResponses;

    private Long quantityProduct;

    public static CartCache fromCartEntity(CartEntity cartEntity) {
        List<CartDetailResponse> cartDetailResponses =
                CartDetailResponse.fromCartDetailEntities(cartEntity.getCartDetailEntities());
        CartCache cartResponse = CartCache.builder()
                .cartDetailResponses(cartDetailResponses)
                .cartId(cartEntity.getId())
                .totalMoney(cartDetailResponses.stream().mapToDouble(CartDetailResponse::getTotalMoney).sum())
                .quantityProduct(cartDetailResponses.stream().mapToLong(CartDetailResponse::getNumberOfProducts).sum())
                .build();
        cartResponse.setCreatedAt(cartEntity.getCreatedAt());
        cartResponse.setUpdatedAt(cartEntity.getUpdatedAt());
        return cartResponse;
    }
}
