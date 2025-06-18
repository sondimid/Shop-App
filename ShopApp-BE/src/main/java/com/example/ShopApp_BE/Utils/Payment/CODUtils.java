package com.example.ShopApp_BE.Utils.Payment;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.OrderDetailDTO;
import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Service.MailService;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Service.ProductService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CODUtils {
    private final JwtTokenUtils jwtTokenUtils;
    private final OrderService orderService;
    private final MailService mailService;
    private final ProductService productService;

    public String createOrder(OrderDTO orderDTO) throws Exception {
        orderDTO.setActive(Boolean.TRUE);
        OrderEntity orderEntity = orderService.createOrder(orderDTO);
        mailService.sendMailOrderCreate(orderEntity);
        return "http://localhost:3000/order/success";
    }
}
