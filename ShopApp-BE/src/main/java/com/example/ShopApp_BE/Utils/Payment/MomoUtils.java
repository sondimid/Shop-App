package com.example.ShopApp_BE.Utils.Payment;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.OrderDetailDTO;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MomoUtils {
    private final JwtTokenUtils jwtTokenUtils;
    private final OrderService orderService;
    private final PayOS payOS;

//    public String createOrder(OrderDTO orderDTO, String authorization) throws Exception {
//
//    }
}
