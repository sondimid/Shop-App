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
import com.example.ShopApp_BE.Service.MailService;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
    private final MailService mailService;

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
        orderEntity.setStatus(OrderStatus.PENDING.toString());
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

    @Override
    public OrderEntity setStatus(Long orderId, String status) throws Exception {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception(MessageKeys.ORDER_NOT_FOUND));
        OrderStatus orderStatus = OrderStatus.fromString(status);
        orderEntity.setStatus(orderStatus.toString());
        mailService.sendEmailOrder(orderEntity);
        return orderRepository.save(orderEntity);
    }

    @Override
    public OrderEntity cancelOrder(String phoneNumber, Long orderId) throws Exception {

        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception(MessageKeys.ORDER_NOT_FOUND));
        if(!orderEntity.getUserEntity().getPhoneNumber().equals(phoneNumber)){
            throw new Exception(MessageKeys.UNAUTHORIZED);
        }
        return setStatus(orderId, OrderStatus.CANCELLED.toString());
    }

    @Override
    public OrderResponse getById(Long orderId, String phoneNumber) throws Exception {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception(MessageKeys.ORDER_NOT_FOUND));
        if(!orderEntity.getUserEntity().getPhoneNumber().equals(phoneNumber)){
            throw new Exception(MessageKeys.UNAUTHORIZED);
        }
        return OrderResponse.fromOrderEntity(orderEntity);
    }

    @Override
    public OrderEntity deleteOrders(List<Long> orderIds) throws Exception {
        for(Long order : orderIds){
            if(!orderRepository.existsById(order)) throw new Exception(MessageKeys.ORDER_NOT_FOUND);
            orderRepository.deleteById(order);
        }
        return null;
    }

    @Override
    public Page<OrderResponse> getByKeyWord(String keyword, String phoneNumber, Pageable pageable) throws Exception {
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new Exception(MessageKeys.USER_ID_NOT_FOUND));
        Page<OrderEntity> orderEntityPage;
        if(userEntity.getRoleEntity().getRole().equals(MessageKeys.ROLE_ADMIN)){
            orderEntityPage = orderRepository.findByKeyword(keyword, pageable);
        }
        else{
            orderEntityPage = orderRepository.findByUserAndKeyword(userEntity.getId(), keyword, pageable);
        }
        return orderEntityPage.map(OrderResponse::fromOrderEntity);
    }
}
