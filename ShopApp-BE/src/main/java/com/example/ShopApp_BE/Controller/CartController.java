package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CartDTO;
import com.example.ShopApp_BE.DTO.ProductDTO;
import com.example.ShopApp_BE.DTO.ProductQuantityDTO;
import com.example.ShopApp_BE.Service.CartService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public ResponseEntity<?> updateCart(@RequestBody CartDTO cartDTO) throws NotFoundException {
        cartService.updateCart(cartDTO);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
    }

    @GetMapping("")
    public ResponseEntity<?> getCart() throws NotFoundException {
        return ResponseEntity.ok().body(cartService.getCart());
    }

    @PutMapping("/number-of-product")
    public ResponseEntity<?> changeNumberOfProduct(@RequestBody ProductQuantityDTO productQuantityDTO) throws NotFoundException {
        cartService.changeNumberOfProduct(productQuantityDTO);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
    }

    @DeleteMapping("/multi/{selectedProducts}")
    public ResponseEntity<?> deleteProducts(@PathVariable("selectedProducts") List<Long> selectedProducts) throws NotFoundException {
        cartService.deleteProducts(selectedProducts);
        return ResponseEntity.accepted().body(MessageKeys.DELETE_PRODUCT_SUCCESS);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") Long productId,
                                           @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) throws NotFoundException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        cartService.deleteProduct(productId);
        return ResponseEntity.accepted().body(MessageKeys.DELETE_PRODUCT_SUCCESS);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> addProduct(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                        @PathVariable("productId") Long productId,
                                        @RequestParam("quantity") Integer quantity) throws NotFoundException, JsonProcessingException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        cartService.addProduct(productId, quantity);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
    }
}
