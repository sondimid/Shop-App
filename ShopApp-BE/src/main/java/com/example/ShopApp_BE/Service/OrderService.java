package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Model.Response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderEntity createOrder(OrderDTO orderDTO, String phoneNumber) throws Exception;

    OrderEntity updateOrder(OrderDTO orderDTO, Long orderId, String phoneNumber) throws Exception;

    Page<OrderResponse> getOrderByUser(String phoneNumber, Pageable pageable) throws Exception;
}
