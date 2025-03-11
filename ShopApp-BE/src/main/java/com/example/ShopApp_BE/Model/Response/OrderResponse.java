package com.example.ShopApp_BE.Model.Response;

import com.example.ShopApp_BE.Model.Entity.OrderDetailEntity;
import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse extends AbstractResponse{
    private String fullName;

    private String email;

    private String phoneNumber;

    private String address;

    private String note;

    private LocalDateTime orderDate;

    private String status;

    private Double totalMoney;

    private String shippingMethod;

    private String shippingAddress;

    private String shippingDate;

    private String trackingNumber;

    private String paymentMethod;

    private UserResponse userResponse;

    private List<OrderDetailResponse> orderDetails;

    public static OrderResponse fromOrderEntity(OrderEntity orderEntity) {
        ModelMapper modelMapper = new ModelMapper();
        OrderResponse orderResponse = modelMapper.map(orderEntity, OrderResponse.class);
        orderResponse.setUserResponse(UserResponse.fromUserEntity(orderEntity.getUserEntity()));
        orderResponse.setOrderDetails(orderEntity.getOrderDetailEntities()
                .stream().map(OrderDetailResponse::fromOrderDetailEntity).toList());
        orderResponse.setCreatedAt(orderEntity.getCreatedAt());
        orderResponse.setUpdatedAt(orderEntity.getUpdatedAt());
        return orderResponse;
    }
}
