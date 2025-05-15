package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;

import com.example.ShopApp_BE.Utils.Payment.CODUtils;
import com.example.ShopApp_BE.Utils.Payment.PayOsUtils;
import com.example.ShopApp_BE.Utils.PaymentMethod;
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
    @PostMapping( "/create-payment-link")
    public ResponseEntity<?> checkout(@RequestBody OrderDTO orderDTO,
                                      @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) throws Exception {
        String checkoutUrl = new String();
        if(PaymentMethod.getPaymentMethod(orderDTO.getPaymentMethod()).equals(PaymentMethod.PAYOS)){
            checkoutUrl = payOsUtils.createOrder(orderDTO, authorization);
        }

        if(PaymentMethod.getPaymentMethod(orderDTO.getPaymentMethod()).equals(PaymentMethod.COD)){
            checkoutUrl = codUtils.createOrder(orderDTO, authorization);
        }
        return ResponseEntity.ok().body(checkoutUrl);
    }
}
