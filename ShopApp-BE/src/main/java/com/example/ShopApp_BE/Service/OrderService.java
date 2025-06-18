package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Model.Response.OrderResponse;
import com.example.ShopApp_BE.Model.Response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderEntity createOrder(OrderDTO orderDTO) throws Exception;

    OrderEntity updateOrder(OrderDTO orderDTO, Long orderId) throws Exception;

    Page<OrderResponse> getOrderByUser(Pageable pageable) throws Exception;

    OrderEntity setStatus(Long orderId, String status) throws Exception;

    OrderEntity cancelOrder(Long orderId) throws Exception;

    OrderResponse getById(Long orderId) throws Exception;

    OrderEntity deleteOrders(List<Long> orderIds) throws Exception;

    Page<OrderResponse> getByKeyWord(String keyword, Pageable pageable) throws Exception;

    void confirmOrder(Long orderId) throws Exception;
}
