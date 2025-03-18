package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CartDTO;
import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Response.CartResponse;

public interface CartService {
    CartEntity updateCart(CartDTO cartDTO, String phoneNumber) throws NotFoundException;

    CartResponse getCart(String phoneNumber) throws NotFoundException;
}
