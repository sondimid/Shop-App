package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CartDTO;
import com.example.ShopApp_BE.DTO.ProductQuantityDTO;
import com.example.ShopApp_BE.Service.CartService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final JwtTokenUtils jwtTokenUtils;
    private final CartService cartService;

    @PutMapping("")
    public ResponseEntity<?> updateCart(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                        @RequestBody CartDTO cartDTO) throws NotFoundException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        cartService.updateCart(cartDTO, email);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
    }

    @GetMapping("")
    public ResponseEntity<?> getCart(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) throws NotFoundException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        return ResponseEntity.ok().body(cartService.getCart(email));
    }

    @PutMapping("/number-of-product")
    public ResponseEntity<?> changeNumberOfProduct(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                                   @RequestBody ProductQuantityDTO productQuantityDTO) throws NotFoundException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        cartService.changeNumberOfProduct(productQuantityDTO, email);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
    }

    @DeleteMapping("/multi/{selectedProducts}")
    public ResponseEntity<?> deleteProducts(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                           @PathVariable("selectedProducts") List<Long> selectedProducts) throws NotFoundException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        cartService.deleteProducts(selectedProducts, email);
        return ResponseEntity.accepted().body(MessageKeys.DELETE_PRODUCT_SUCCESS);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") Long productId,
                                           @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) throws NotFoundException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        cartService.deleteProduct(productId, email);
        return ResponseEntity.accepted().body(MessageKeys.DELETE_PRODUCT_SUCCESS);
    }
}
