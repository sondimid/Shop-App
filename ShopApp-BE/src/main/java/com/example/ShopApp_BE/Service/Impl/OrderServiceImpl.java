package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.UnauthorizedAccessException;
import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.OrderDetailDTO;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public OrderEntity createOrder(OrderDTO orderDTO, String email) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        OrderEntity orderEntity = modelMapper.map(orderDTO, OrderEntity.class);
        orderEntity.setUserEntity(userEntity);
        orderEntity.setOrderDetailEntities(new ArrayList<>());
        for(OrderDetailDTO orderDetailDTO : orderDTO.getOrderDetailDTOs()){
            ProductEntity productEntity = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                    () -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));
            OrderDetailEntity orderDetailEntity = OrderDetailEntity.builder()
                    .color(orderDetailDTO.getColor())
                    .price(productEntity.getPrice())
                    .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                    .discount(productEntity.getDiscount())
                    .orderEntity(orderEntity)
                    .productEntity(productEntity)
                    .totalMoney(orderDetailDTO.getTotalMoney())
                    .build();
            orderEntity.getOrderDetailEntities().add(orderDetailEntity);
        }
        orderEntity.setStatus(OrderStatus.PENDING.toString());
        return orderRepository.save(orderEntity);
    }

    @Override
    public OrderEntity updateOrder(OrderDTO orderDTO, Long orderId, String email) throws Exception {
        OrderEntity orderEntity = orderRepository.findByCode(orderId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.ORDER_NOT_FOUND));
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        if(!Objects.equals(orderEntity.getUserEntity().getId(), userEntity.getId())){
            throw new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED);
        }
        orderEntity.setPhoneNumber(orderDTO.getPhoneNumber());
        orderEntity.setAddress(orderDTO.getAddress());
        orderEntity.setFullName(orderDTO.getFullName());
        return orderRepository.save(orderEntity);
    }

    @Override
    public Page<OrderResponse> getOrderByUser(String email, Pageable pageable) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        Page<OrderEntity> orderEntityPage = orderRepository.findByUserEntity_Id(userEntity.getId(),pageable);
        return orderEntityPage.map(OrderResponse::fromOrderEntity);
    }

    @Override
    public OrderEntity setStatus(Long orderId, String status) throws Exception {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.ORDER_NOT_FOUND));
        OrderStatus orderStatus = OrderStatus.fromString(status);
        orderEntity.setStatus(orderStatus.toString());
        mailService.sendEmailOrder(orderEntity);
        return orderRepository.save(orderEntity);
    }

    @Override
    public OrderEntity cancelOrder(String email, Long orderId) throws Exception {

        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.ORDER_NOT_FOUND));
        if(!orderEntity.getUserEntity().getEmail().equals(email)){
            throw new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED);
        }
        if(OrderStatus.fromString(orderEntity.getStatus()).getStatusCode() <= 2)
            return setStatus(orderId, OrderStatus.CANCELLED.toString());
        throw new UnauthorizedAccessException(MessageKeys.CANNOT_CANCEL_ORDER);
    }

    @Override
    public OrderResponse getById(Long orderId, String phoneNumber) throws Exception {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.ORDER_NOT_FOUND));
        if(!orderEntity.getUserEntity().getPhoneNumber().equals(phoneNumber)){
            throw new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED);
        }
        return OrderResponse.fromOrderEntity(orderEntity);
    }

    @Override
    public OrderEntity deleteOrders(List<Long> orderIds) throws Exception {
        for(Long order : orderIds){
            if(!orderRepository.existsById(order)) throw new NotFoundException(MessageKeys.ORDER_NOT_FOUND);
            orderRepository.deleteById(order);
        }
        return null;
    }

    @Override
    public Page<OrderResponse> getByKeyWord(String keyword, String email, Pageable pageable) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(email)
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

    @Override
    public void confirmOrder(Long orderId) throws Exception {
        OrderEntity orderEntity = orderRepository.findByCode(orderId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.ORDER_NOT_FOUND));
        orderEntity.setActive(true);
        orderEntity.getUserEntity().getCartEntity().getCartDetailEntities().clear();
        mailService.sendEmailOrder(orderEntity);
    }
}
