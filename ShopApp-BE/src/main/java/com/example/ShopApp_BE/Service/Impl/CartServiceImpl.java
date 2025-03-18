package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CartDTO;
import com.example.ShopApp_BE.DTO.CartDetailDTO;
import com.example.ShopApp_BE.Model.Entity.CartDetailEntity;
import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.CartResponse;
import com.example.ShopApp_BE.Repository.CartDetailRepository;
import com.example.ShopApp_BE.Repository.CartRepository;
import com.example.ShopApp_BE.Repository.ProductRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.CartService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ModelMapper modelMapper;

    @Override
    public CartEntity updateCart(CartDTO cartDTO, String phoneNumber) throws NotFoundException {
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        CartEntity cartEntity = userEntity.getCartEntity();
        if(cartEntity == null){
            cartEntity = CartEntity.builder()
                    .cartDetailEntities(new ArrayList<>())
                    .userEntity(userEntity)
                    .build();
        }
        else{
            cartDetailRepository.deleteByCartEntity_Id(cartEntity.getId());
//            cartEntity.setUpdatedAt(LocalDateTime.now());
        }
        if(cartDTO.getCartDetailDTOS() == null) cartDTO.setCartDetailDTOS(new ArrayList<>());
        for(CartDetailDTO cartDetailDTO: cartDTO.getCartDetailDTOS()){
            ProductEntity productEntity = productRepository.findById(cartDetailDTO.getProductId())
                    .orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));
            CartDetailEntity cartDetailEntity = modelMapper.map(cartDetailDTO, CartDetailEntity.class);
            cartDetailEntity.setProductEntity(productEntity);
            cartDetailEntity.setCartEntity(cartEntity);
            cartEntity.getCartDetailEntities().add(cartDetailEntity);
        }
        userEntity.setCartEntity(cartEntity);
        userRepository.save(userEntity);
        return cartRepository.save(cartEntity);
    }

    @Override
    public CartResponse getCart(String phoneNumber) throws NotFoundException {
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        return CartResponse.fromCartEntity(userEntity.getCartEntity());
    }
}
