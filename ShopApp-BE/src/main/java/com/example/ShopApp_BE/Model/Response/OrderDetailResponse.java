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

    private String name;

    private Integer numberOfProducts;

    private Double totalMoney;

    private String color;

    private Long productId;

    private Long orderId;

    private Double discount;

    private String image;

    public static OrderDetailResponse fromOrderDetailEntity(OrderDetailEntity orderDetailEntity) {
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                .orderId(orderDetailEntity.getOrderEntity().getId())
                .productId(orderDetailEntity.getProductEntity().getId())
                .color(orderDetailEntity.getColor())
                .numberOfProducts(orderDetailEntity.getNumberOfProducts())
                .totalMoney(orderDetailEntity.getTotalMoney())
                .price(orderDetailEntity.getProductEntity().getFinalPrice())
                .name(orderDetailEntity.getProductEntity().getName())
                .discount(orderDetailEntity.getDiscount())
                .image(orderDetailEntity.getProductEntity().getImageEntities().get(0).getUrl())
                .build();
        orderDetailResponse.setCreatedAt(orderDetailEntity.getCreatedAt());
        orderDetailResponse.setUpdatedAt(orderDetailEntity.getUpdatedAt());
        return orderDetailResponse;
    }
}