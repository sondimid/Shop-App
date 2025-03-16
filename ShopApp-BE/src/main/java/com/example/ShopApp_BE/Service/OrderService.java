package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Model.Response.OrderResponse;
import com.example.ShopApp_BE.Model.Response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderEntity createOrder(OrderDTO orderDTO, String phoneNumber) throws Exception;

    OrderEntity updateOrder(OrderDTO orderDTO, Long orderId, String phoneNumber) throws Exception;

    Page<OrderResponse> getOrderByUser(String phoneNumber, Pageable pageable) throws Exception;

    OrderEntity setStatus(Long orderId, String status) throws Exception;

    OrderEntity cancelOrder(String phoneNumber, Long orderId) throws Exception;

    OrderResponse getById(Long orderId, String s) throws Exception;

    OrderEntity deleteOrders(List<Long> orderIds) throws Exception;

    Page<OrderResponse> getByKeyWord(String status, String phoneNumber, Pageable pageable) throws Exception;
}
