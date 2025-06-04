package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CartDTO;
import com.example.ShopApp_BE.DTO.ProductQuantityDTO;
import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Response.CartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface CartService {
    CartEntity updateCart(CartDTO cartDTO, String email) throws NotFoundException;

    CartResponse getCart(String email) throws NotFoundException;

    CartEntity changeNumberOfProduct(ProductQuantityDTO productQuantityDTO, String email) throws NotFoundException;

    void deleteProducts(List<Long> productId, String email) throws NotFoundException;

    void deleteProduct(Long productId, String email) throws NotFoundException;

    void addProduct(String email, Long productId, Integer quantity) throws NotFoundException, JsonProcessingException;
}
