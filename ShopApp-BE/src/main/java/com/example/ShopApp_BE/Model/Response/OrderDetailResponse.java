package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.OrderDetailEntity;
import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse extends AbstractResponse{
    private Double price;

    private Integer numberOfProducts;

    private Double totalMoney;

    private String color;

    private Long productId;

    private Long orderId;

    public static OrderDetailResponse fromOrderDetailEntity(OrderDetailEntity orderDetailEntity) {
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                .orderId(orderDetailEntity.getOrderEntity().getId())
                .productId(orderDetailEntity.getProductEntity().getId())
                .color(orderDetailEntity.getColor())
                .numberOfProducts(orderDetailEntity.getNumberOfProducts())
                .totalMoney(orderDetailEntity.getTotalMoney())
                .price(orderDetailEntity.getPrice()).build();
        orderDetailResponse.setCreatedAt(orderDetailEntity.getCreatedAt());
        orderDetailResponse.setUpdatedAt(orderDetailEntity.getUpdatedAt());
        return orderDetailResponse;
    }
}