package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Utils.CookieUtils;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;

import com.example.ShopApp_BE.Utils.Payment.CODUtils;
import com.example.ShopApp_BE.Utils.Payment.PayOsUtils;
import com.example.ShopApp_BE.Utils.PaymentMethod;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;

@RestController
@RequestMapping("${api.prefix}/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final PayOsUtils payOsUtils;
    private final CODUtils codUtils;
    private final CookieUtils cookieUtils;

    @PostMapping( "/create-payment-link")
    public ResponseEntity<?> checkout(@RequestBody OrderDTO orderDTO,
                                      HttpServletResponse response) throws Exception {
        String checkoutUrl = "";
        if(PaymentMethod.getPaymentMethod(orderDTO.getPaymentMethod()).equals(PaymentMethod.PAYOS)){
            checkoutUrl = payOsUtils.createOrder(orderDTO);
        }

        if(PaymentMethod.getPaymentMethod(orderDTO.getPaymentMethod()).equals(PaymentMethod.COD)){
            checkoutUrl = codUtils.createOrder(orderDTO);
        }
        Cookie cookie = cookieUtils.getOrderCodeCookie(orderDTO.getCode());
        response.addCookie(cookie);
        return ResponseEntity.ok().body(checkoutUrl);
    }
}
