package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CartDTO;
import com.example.ShopApp_BE.DTO.ProductQuantityDTO;
import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Response.CartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface CartService {
    CartEntity updateCart(CartDTO cartDTO) throws NotFoundException;

    CartResponse getCart() throws NotFoundException;

    CartEntity changeNumberOfProduct(ProductQuantityDTO productQuantityDTO) throws NotFoundException;

    void deleteProducts(List<Long> productId) throws NotFoundException;

    void deleteProduct(Long productIdl) throws NotFoundException;

    void addProduct(Long productId, Integer quantity) throws NotFoundException, JsonProcessingException;
}
