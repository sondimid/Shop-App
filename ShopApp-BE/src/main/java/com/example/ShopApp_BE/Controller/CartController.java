package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CartDTO;
import com.example.ShopApp_BE.Service.CartService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final JwtTokenUtils jwtTokenUtils;
    private final CartService cartService;

    @PutMapping("")
    public ResponseEntity<?> updateCart(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                        @RequestBody CartDTO cartDTO) throws NotFoundException {
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS);
        cartService.updateCart(cartDTO, phoneNumber);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
    }

    @GetMapping("")
    public ResponseEntity<?> getCart(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) throws NotFoundException {
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS);
        return ResponseEntity.ok().body(cartService.getCart(phoneNumber));
    }
}
