package com.example.ShopApp_BE.Utils.Payment;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.OrderDetailDTO;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PayOsUtils {
    private final JwtTokenUtils jwtTokenUtils;
    private final OrderService orderService;
    private final PayOS payOS;

    public String createOrder(OrderDTO orderDTO, String authorization) throws Exception {
        final String baseUrl = "http://localhost:3000";
        final String returnUrl = baseUrl + "/order/success";
        final String cancelUrl = baseUrl + "/cart";
        List<ItemData> items = new ArrayList<>();
        for(OrderDetailDTO orderDetailDTO: orderDTO.getOrderDetailDTOs()){
            ItemData item = ItemData.builder()
                    .name(orderDetailDTO.getName())
                    .price((int)Math.round(orderDetailDTO.getPrice()))
                    .quantity(orderDetailDTO.getNumberOfProducts())
                    .build();
            items.add(item);
        }
        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderDTO.getCode())
                .amount((int) Math.round(orderDTO.getTotalMoney()))
                .description("Thanh Toan Don Hang")
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .items(items)
                .build();
        CheckoutResponseData data = payOS.createPaymentLink(paymentData);
        String checkoutUrl = data.getCheckoutUrl();
        orderDTO.setActive(Boolean.FALSE);
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        return checkoutUrl;
    }
}
