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
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if(userEntity.getCartEntity() == null){
            return new CartResponse();
        }
//        List<CartDetailEntity> cartDetailEntities = cartDetailRepository
//                .findByCartEntity_Id(userEntity.getCartEntity().getId());
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
}
