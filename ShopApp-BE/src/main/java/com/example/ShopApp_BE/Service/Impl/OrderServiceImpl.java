package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.OrderDetailDTO;
import com.example.ShopApp_BE.Model.Entity.OrderDetailEntity;
import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.OrderResponse;
import com.example.ShopApp_BE.Repository.OrderDetailRepository;
import com.example.ShopApp_BE.Repository.OrderRepository;
import com.example.ShopApp_BE.Repository.ProductRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Service.UserService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public OrderEntity createOrder(OrderDTO orderDTO, String phoneNumber) throws Exception {
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new Exception(MessageKeys.USER_ID_NOT_FOUND));
        OrderEntity orderEntity = modelMapper.map(orderDTO, OrderEntity.class);
        orderEntity.setUserEntity(userEntity);
        orderEntity.setOrderDetailEntities(new ArrayList<>());
        for(OrderDetailDTO orderDetailDTO : orderDTO.getOrderDetailDTOs()){
            ProductEntity productEntity = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                    () -> new Exception(MessageKeys.PRODUCT_NOT_FOUND));
            OrderDetailEntity orderDetailEntity = modelMapper.map(orderDetailDTO,OrderDetailEntity.class);
            orderDetailEntity.setOrderEntity(orderEntity);
            orderDetailEntity.setProductEntity(productEntity);
            orderEntity.getOrderDetailEntities().add(orderDetailEntity);
            orderDetailRepository.save(orderDetailEntity);
        }
        return orderRepository.save(orderEntity);
    }

    @Override
    public OrderEntity updateOrder(OrderDTO orderDTO, Long orderId, String phoneNumber) throws Exception {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception(MessageKeys.ORDER_NOT_FOUND));
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new Exception(MessageKeys.USER_ID_NOT_FOUND));
        orderEntity = modelMapper.map(orderDTO, OrderEntity.class);
        orderEntity.setUserEntity(userEntity);
        orderEntity.setOrderDetailEntities(new ArrayList<>());
        for(OrderDetailDTO orderDetailDTO : orderDTO.getOrderDetailDTOs()){
            ProductEntity productEntity = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                    () -> new Exception(MessageKeys.PRODUCT_NOT_FOUND)
            );
            OrderDetailEntity orderDetailEntity = modelMapper.map(orderDetailDTO,OrderDetailEntity.class);
            orderDetailEntity.setOrderEntity(orderEntity);
            orderDetailEntity.setProductEntity(productEntity);
            orderEntity.getOrderDetailEntities().add(orderDetailEntity);
            orderDetailRepository.save(orderDetailEntity);
        }
        return orderRepository.save(orderEntity);

    }

    @Override
    public Page<OrderResponse> getOrderByUser(String phoneNumber, Pageable pageable) throws Exception {
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new Exception(MessageKeys.USER_ID_NOT_FOUND));
        Page<OrderEntity> orderEntityPage = orderRepository.findByUserEntity_Id(userEntity.getId(),pageable);
        return orderEntityPage.map(OrderResponse::fromOrderEntity);
    }
}
