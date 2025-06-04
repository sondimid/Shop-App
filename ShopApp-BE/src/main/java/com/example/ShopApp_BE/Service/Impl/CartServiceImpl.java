package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CartDTO;
import com.example.ShopApp_BE.DTO.CartDetailDTO;
import com.example.ShopApp_BE.DTO.ProductQuantityDTO;
import com.example.ShopApp_BE.Model.Entity.CartDetailEntity;
import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.CartDetailResponse;
import com.example.ShopApp_BE.Model.Response.CartResponse;
import com.example.ShopApp_BE.Repository.CartDetailRepository;
import com.example.ShopApp_BE.Repository.CartRepository;
import com.example.ShopApp_BE.Repository.ProductRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.CartService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.ShopApp_BE.Utils.MessageKeys.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ModelMapper modelMapper;
    private final RedisServiceImpl<String, String, Object> redisService;
    static final String QUANTITY_HASH = "quantity";

    @Override
    public CartEntity updateCart(CartDTO cartDTO, String email) throws NotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        CartEntity cartEntity = userEntity.getCartEntity();

        for(Long id : cartDTO.getIdDeletes()){
            boolean isPresent = cartEntity.getCartDetailEntities()
                    .removeIf(cartDetail -> cartDetail.getId().equals(id));
            if(!isPresent) throw new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND);

            cartDetailRepository.deleteById(id);
        }

        for(CartDetailDTO cartDetailDTO: cartDTO.getCartDetailDTOS()){
            ProductEntity productEntity = productRepository.findById(cartDetailDTO.getProductId())
                    .orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));

            Optional<CartDetailEntity> cartDetailEntityOptional = cartEntity.getCartDetailEntities()
                    .stream()
                    .filter(cartDetail -> cartDetail.getProductEntity().getId().equals(productEntity.getId()))
                    .findFirst();

            if(cartDetailEntityOptional.isPresent()){
                cartDetailEntityOptional.get().setColor(cartDetailDTO.getColor());
                cartDetailEntityOptional.get().setNumberOfProducts(cartDetailEntityOptional.get().getNumberOfProducts() + cartDetailDTO.getNumberOfProducts());
                continue;
            }
            CartDetailEntity cartDetailEntity = modelMapper.map(cartDetailDTO, CartDetailEntity.class);
            cartDetailEntity.setProductEntity(productEntity);
            cartDetailEntity.setCartEntity(cartEntity);
            cartEntity.getCartDetailEntities().add(cartDetailEntity);

        }
        return cartRepository.save(cartEntity);
    }

    @Override
    public CartResponse getCart(String email) throws NotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        Map<Long, CartDetailResponse> cartDetailResponseMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        if(redisService.hasKey(CART_HASH + email)){
            Map<String, Object> maps = redisService.hashGet(CART_HASH + email);
            CartResponse cartResponse = new CartResponse();
            for(Map.Entry<String, Object> map: maps.entrySet()){
                String key = map.getKey();
                Object value = map.getValue();
                if(key.contains(QUANTITY_HASH)){
                    Long productId = Long.valueOf(key.substring(15));
                    CartDetailResponse cartDetailResponse = cartDetailResponseMap.getOrDefault(productId,
                            CartDetailResponse.builder()
                                    .numberOfProducts((Integer) value).build());
                    cartDetailResponseMap.put(productId, cartDetailResponse);
                    continue;
                }
                Long productId = Long.valueOf(key.substring(7));
                CartDetailResponse cartDetailResponse = mapper.convertValue(value, CartDetailResponse.class);
                cartDetailResponseMap.put(productId, cartDetailResponse);
            }

        }

        return CartResponse.fromCartEntity(userEntity.getCartEntity());
    }

    @Override
    public CartEntity changeNumberOfProduct(ProductQuantityDTO productQuantityDTO, String email) throws NotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        CartEntity cartEntity = userEntity.getCartEntity();
        cartEntity.getCartDetailEntities().stream()
                .filter(cartDetail -> cartDetail.getProductEntity().getId().equals(productQuantityDTO.getProductId()))
                .findFirst()
                .ifPresent(cartDetail -> cartDetail.setNumberOfProducts(productQuantityDTO.getNumberOfProduct()));
        return cartRepository.save(cartEntity);
    }

    @Override
    public void deleteProducts(List<Long> productsId, String email) throws NotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        CartEntity cartEntity = userEntity.getCartEntity();
        for(Long productId : productsId){
            CartDetailEntity cartDetailEntity = cartEntity.getCartDetailEntities().stream()
                    .filter(cartDetail -> cartDetail.getProductEntity().getId().equals(productId))
                    .findFirst().get();
            cartEntity.getCartDetailEntities().remove(cartDetailEntity);
        }
        cartRepository.save(cartEntity);
    }

    @Override
    public void deleteProduct(Long productId, String email) throws NotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        CartEntity cartEntity = userEntity.getCartEntity();
        CartDetailEntity cartDetailEntity = cartEntity.getCartDetailEntities().stream()
                .filter(cartDetail -> cartDetail.getProductEntity().getId().equals(productId))
                .findFirst().get();
        cartEntity.getCartDetailEntities().remove(cartDetailEntity);
        cartRepository.save(cartEntity);
    }

    @Override
    public void addProduct(String email, Long productId, Integer quantity) throws NotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));

        redisService.setTimeToLive(CART_HASH + email, 5 * 60 * 1000);

        if(redisService.hashHasKey(CART_HASH + email, PRODUCT_HASH + productId + QUANTITY_HASH)) {
            redisService.hashIncrement(CART_HASH + email, PRODUCT_HASH + productId + QUANTITY_HASH, quantity);
            CartDetailEntity cartDetailEntity = userEntity.getCartEntity().getCartDetailEntities().stream()
                    .filter(cartDetail -> cartDetail.getProductEntity().getId().equals(productId))
                    .findFirst()
                    .get();
            cartDetailEntity.setNumberOfProducts(cartDetailEntity.getNumberOfProducts() + quantity);
            userRepository.save(userEntity);

            return;
        }

        CartDetailEntity cartDetailEntity = CartDetailEntity.builder()
                .numberOfProducts(quantity)
                .productEntity(productEntity)
                .build();
        userEntity.getCartEntity().getCartDetailEntities().add(cartDetailEntity);
        redisService.hashSet(CART_HASH + email, PRODUCT_HASH + QUANTITY_HASH + productId, quantity);
        String json = mapper.writeValueAsString(CartDetailResponse.fromCartDetailEntity(cartDetailEntity));
        redisService.hashSet(CART_HASH + email, PRODUCT_HASH + productId, json);
        userRepository.save(userEntity);
    }
}
